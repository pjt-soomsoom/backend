package com.soomsoom.backend.adapter.out.persistence.appversion.repository.jpa

import com.soomsoom.backend.adapter.out.persistence.appversion.repository.jpa.entity.AppVersionJpaEntity
import org.springframework.data.jpa.repository.JpaRepository

interface AppVersionJpaRepository : JpaRepository<AppVersionJpaEntity, Long>
