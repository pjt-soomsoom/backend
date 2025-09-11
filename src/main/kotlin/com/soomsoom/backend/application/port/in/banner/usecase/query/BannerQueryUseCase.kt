package com.soomsoom.backend.application.port.`in`.banner.usecase.query

import com.soomsoom.backend.application.port.`in`.banner.dto.BannerAdminResult
import com.soomsoom.backend.application.port.`in`.banner.dto.BannerResult
import com.soomsoom.backend.application.port.`in`.banner.query.FindBannersCriteria
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface BannerQueryUseCase {
    fun findActiveBanners(): List<BannerResult>
    fun findAllBanners(criteria: FindBannersCriteria, pageable: Pageable): Page<BannerAdminResult>
}
