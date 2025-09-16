package com.soomsoom.backend.adapter.out.persistence.banner

import com.soomsoom.backend.adapter.out.persistence.banner.repository.jpa.BannerJpaRepository
import com.soomsoom.backend.adapter.out.persistence.banner.repository.jpa.BannerQueryDslRepository
import com.soomsoom.backend.application.port.`in`.banner.query.FindBannersCriteria
import com.soomsoom.backend.application.port.out.banner.BannerPort
import com.soomsoom.backend.domain.banner.model.Banner
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Component

@Component
class BannerPersistenceAdapter(
    private val bannerJpaRepository: BannerJpaRepository,
    private val bannerQueryDslRepository: BannerQueryDslRepository,
) : BannerPort {
    override fun save(banner: Banner): Banner {
        val savedEntity = bannerJpaRepository.save(banner.toEntity())
        return savedEntity.toDomain()
    }

    override fun findById(bannerId: Long): Banner? {
        return bannerJpaRepository.findById(bannerId)
            .map { it.toDomain() }
            .orElse(null)
    }

    override fun findByIds(bannerIds: List<Long>): List<Banner> {
        return bannerJpaRepository.findAllById(bannerIds).map { it.toDomain() }
    }

    override fun countActiveBanners(): Long {
        return bannerQueryDslRepository.countActiveBanners()
    }

    override fun shiftOrdersForCreate(order: Int) {
        bannerQueryDslRepository.shiftOrdersForCreate(order)
    }

    override fun shiftOrdersForDelete(order: Int) {
        bannerQueryDslRepository.shiftOrdersForDelete(order)
    }

    override fun saveAll(banners: List<Banner>): List<Banner> {
        val entities = banners.map { it.toEntity() }
        return bannerJpaRepository.saveAll(entities).map { it.toDomain() }
    }

    override fun findActiveBanners(): List<Banner> {
        return bannerQueryDslRepository.findActiveBanners().map { it.toDomain() }
    }

    override fun findAll(criteria: FindBannersCriteria, pageable: Pageable): Page<Banner> {
        return bannerQueryDslRepository.findAll(criteria.deletionStatus, pageable)
            .map { it.toDomain() }
    }

    override fun findLastActiveDisplayOrder(): Int {
        return bannerQueryDslRepository.countActiveBanners().toInt()
    }
}
