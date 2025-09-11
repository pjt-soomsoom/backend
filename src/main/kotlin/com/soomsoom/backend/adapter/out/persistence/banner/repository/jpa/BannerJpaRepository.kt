package com.soomsoom.backend.adapter.out.persistence.banner.repository.jpa

import com.soomsoom.backend.adapter.out.persistence.banner.repository.jpa.entity.BannerJpaEntity
import org.springframework.data.jpa.repository.JpaRepository

interface BannerJpaRepository : JpaRepository<BannerJpaEntity, Long>
