package com.soomsoom.backend.application.service.banner.command

import com.soomsoom.backend.application.port.`in`.banner.usecase.command.DeleteBannerUseCase
import com.soomsoom.backend.application.port.out.banner.BannerPort
import com.soomsoom.backend.common.exception.SoomSoomException
import com.soomsoom.backend.domain.banner.BannerErrorCode
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class DeleteBannerService(
    private val bannerPort: BannerPort,
) : DeleteBannerUseCase {

    @PreAuthorize("hasRole('ADMIN')")
    override fun delete(bannerId: Long) {
        val banner = bannerPort.findById(bannerId)
            ?: throw SoomSoomException(BannerErrorCode.NOT_FOUND)

        if (banner.isActive) {
            bannerPort.shiftOrdersForDelete(banner.displayOrder)
        }

        banner.delete()
        bannerPort.save(banner)
    }
}
