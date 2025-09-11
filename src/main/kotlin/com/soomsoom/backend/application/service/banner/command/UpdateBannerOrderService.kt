package com.soomsoom.backend.application.service.banner.command

import com.soomsoom.backend.application.port.`in`.banner.command.UpdateBannerOrderCommand
import com.soomsoom.backend.application.port.`in`.banner.dto.BannerAdminResult
import com.soomsoom.backend.application.port.`in`.banner.dto.toAdminResult
import com.soomsoom.backend.application.port.`in`.banner.usecase.command.UpdateBannerOrderUseCase
import com.soomsoom.backend.application.port.out.activity.ActivityPort
import com.soomsoom.backend.application.port.out.banner.BannerPort
import com.soomsoom.backend.common.exception.SoomSoomException
import com.soomsoom.backend.domain.activity.ActivityErrorCode
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class UpdateBannerOrderService(
    private val bannerPort: BannerPort,
    private val activityPort: ActivityPort,
) : UpdateBannerOrderUseCase {

    @PreAuthorize("hasRole('ADMIN')")
    override fun updateOrder(command: UpdateBannerOrderCommand): List<BannerAdminResult> {
        val requestedBannerIds = command.orderedBannerIds

        // 현재 활성화 되어있는 모든 배너를 조회
        val allActiveBanners = bannerPort.findActiveBanners()

        // 요청으로 들어온 배너 ID 목록과 현재 활성화된 배너 ID 목록을 비교
        val bannersToUpdate = allActiveBanners.filter { it.id in requestedBannerIds }
        val bannersToDeactivate = allActiveBanners.filterNot { it.id in requestedBannerIds }

        // 요청 목록에 포함되지 않은 배너들은 비활성화 처리
        bannersToDeactivate.forEach { banner ->
            banner.isActive = false
            banner.displayOrder = 0 // 순서 목록에서 제외
        }

        // 요청 목록에 포함된 배너들은 새로운 순서를 부여하고 활성화 상태를 유지
        val bannerMap = bannersToUpdate.associateBy { it.id }
        requestedBannerIds.forEachIndexed { index, bannerId ->
            bannerMap[bannerId]?.let { banner ->
                banner.displayOrder = index + 1
                banner.isActive = true
            }
        }

        // 변경된 모든 배너 정보(비활성화 + 순서 변경)를 한 번에 저장
        val bannersToSave = bannersToUpdate + bannersToDeactivate
        val savedBanners = bannerPort.saveAll(bannersToSave)
            .filter { it.isActive } // 활성화된 배너만 필터링
            .sortedBy { it.displayOrder } // 최종적으로 순서대로 정렬

        // DTO로 변환하여 반환합니다.
        val activityIds = savedBanners.map { it.linkedActivityId }.distinct()
        val activities = activityPort.findByIds(activityIds).associateBy { it.id }

        return savedBanners.map { banner ->
            val activity = activities[banner.linkedActivityId]
                ?: throw SoomSoomException(ActivityErrorCode.NOT_FOUND)
            banner.toAdminResult(activity)
        }
    }
}
