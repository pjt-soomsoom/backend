package com.soomsoom.backend.application.service.banner.query

import com.soomsoom.backend.application.port.`in`.banner.dto.BannerAdminResult
import com.soomsoom.backend.application.port.`in`.banner.dto.BannerResult
import com.soomsoom.backend.application.port.`in`.banner.dto.toAdminResult
import com.soomsoom.backend.application.port.`in`.banner.dto.toResult
import com.soomsoom.backend.application.port.`in`.banner.query.FindBannersCriteria
import com.soomsoom.backend.application.port.`in`.banner.usecase.query.BannerQueryUseCase
import com.soomsoom.backend.application.port.out.activity.ActivityPort
import com.soomsoom.backend.application.port.out.banner.BannerPort
import com.soomsoom.backend.common.exception.SoomSoomException
import com.soomsoom.backend.domain.activity.ActivityErrorCode
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class FindBannerService(
    private val bannerPort: BannerPort,
    private val activityPort: ActivityPort,
) : BannerQueryUseCase {

    override fun findActiveBanners(): List<BannerResult> {
        val banners = bannerPort.findActiveBanners()
        if (banners.isEmpty()) return emptyList()

        val activityIds = banners.map { it.linkedActivityId }.distinct()
        val activities = activityPort.findByIds(activityIds).associateBy { it.id }

        return banners.mapNotNull { banner ->
            activities[banner.linkedActivityId]?.let { activity ->
                banner.toResult(activity)
            }
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    override fun findAllBanners(criteria: FindBannersCriteria, pageable: Pageable): Page<BannerAdminResult> {
        val bannerPage = bannerPort.findAll(criteria, pageable)
        if (bannerPage.content.isEmpty()) return Page.empty()

        val activityIds = bannerPage.content.map { it.linkedActivityId }.distinct()
        val activities = activityPort.findByIds(activityIds).associateBy { it.id }

        return bannerPage.map { banner ->
            val activity = activities[banner.linkedActivityId]
                ?: throw SoomSoomException(ActivityErrorCode.NOT_FOUND)
            banner.toAdminResult(activity)
        }
    }
}
