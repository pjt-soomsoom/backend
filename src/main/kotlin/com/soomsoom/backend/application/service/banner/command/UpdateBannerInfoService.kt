package com.soomsoom.backend.application.service.banner.command

import com.soomsoom.backend.application.port.`in`.banner.command.UpdateBannerInfoCommand
import com.soomsoom.backend.application.port.`in`.banner.dto.BannerAdminResult
import com.soomsoom.backend.application.port.`in`.banner.dto.toAdminResult
import com.soomsoom.backend.application.port.`in`.banner.usecase.command.UpdateBannerInfoUseCase
import com.soomsoom.backend.application.port.out.activity.ActivityPort
import com.soomsoom.backend.application.port.out.banner.BannerPort
import com.soomsoom.backend.common.exception.SoomSoomException
import com.soomsoom.backend.domain.activity.ActivityErrorCode
import com.soomsoom.backend.domain.banner.BannerErrorCode
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class UpdateBannerInfoService(
    private val bannerPort: BannerPort,
    private val activityPort: ActivityPort,
) : UpdateBannerInfoUseCase {

    @PreAuthorize("hasRole('ADMIN')")
    override fun updateInfo(command: UpdateBannerInfoCommand): BannerAdminResult {
        val banner = bannerPort.findById(command.bannerId)
            ?: throw SoomSoomException(BannerErrorCode.NOT_FOUND)

        val originalOrder = banner.displayOrder
        val targetOrder = command.displayOrder

        if (targetOrder != null && originalOrder != targetOrder) {
            val maxOrder = bannerPort.countActiveBanners()
            if (targetOrder !in 1..maxOrder) {
                throw SoomSoomException(BannerErrorCode.INVALID_DISPLAY_ORDER)
            }

            bannerPort.shiftOrdersForDelete(originalOrder)
            bannerPort.shiftOrdersForCreate(targetOrder)
            banner.displayOrder = targetOrder
        }

        // 활성화 상태가 변경될 때 순서 재정렬
        if (banner.isActive && !command.isActive) { // 활성 -> 비활성
            // 기존 순서 뒤의 배너들을 앞으로 당김
            bannerPort.shiftOrdersForDelete(banner.displayOrder)
            // 비활성화된 배너는 순서를 0으로 설정하여 순서 목록에서 제외
            banner.displayOrder = 0
        } else if (!banner.isActive && command.isActive) { // 비활성 -> 활성
            // 활성화되는 배너는 가장 마지막 순서로 배치
            banner.displayOrder = bannerPort.findLastActiveDisplayOrder() + 1
        }

        banner.update(
            description = command.description,
            buttonText = command.buttonText,
            linkedActivityId = command.linkedActivityId,
            displayOrder = banner.displayOrder,
            isActive = command.isActive
        )
        val savedBanner = bannerPort.save(banner)

        val activity = activityPort.findById(savedBanner.linkedActivityId)
            ?: throw SoomSoomException(ActivityErrorCode.NOT_FOUND)

        return savedBanner.toAdminResult(activity)
    }
}
