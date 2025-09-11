package com.soomsoom.backend.application.port.out.banner

import com.soomsoom.backend.application.port.`in`.banner.query.FindBannersCriteria
import com.soomsoom.backend.domain.banner.model.Banner
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface BannerPort {
    fun save(banner: Banner): Banner
    fun findById(bannerId: Long): Banner?
    fun findByIds(bannerIds: List<Long>): List<Banner>

    /**
     * 활성화된 모든 배너를 순서대로 조회합니다. (클라이언트용)
     */
    fun findActiveBanners(): List<Banner>

    /**
     * 모든 배너를 조건에 따라 페이징하여 조회합니다. (관리자용)
     */
    fun findAll(criteria: FindBannersCriteria, pageable: Pageable): Page<Banner>

    /**
     * 현재 활성화된 배너 중 가장 마지막 순서 번호를 조회합니다.
     */
    fun findLastActiveDisplayOrder(): Int

    /**
     * 특정 순서 이후의 모든 활성화된 배너들의 순서를 1씩 증가시킵니다. (삽입 시)
     */
    fun shiftOrdersForCreate(order: Int)

    /**
     * 특정 순서 이후의 모든 활성화된 배너들의 순서를 1씩 감소시킵니다. (삭제/비활성화 시)
     */
    fun shiftOrdersForDelete(order: Int)

    /**
     * 여러 배너 정보를 한 번에 저장합니다. (순서 변경 시 사용)
     */
    fun saveAll(banners: List<Banner>): List<Banner>

    /**
     * 현재 활성화된 배너의 총 개수를 조회
     */
    fun countActiveBanners(): Long
}
