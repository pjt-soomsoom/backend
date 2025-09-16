package com.soomsoom.backend.adapter.out.persistence.useractivity.repository.jpa

import com.soomsoom.backend.adapter.out.persistence.useractivity.repository.jpa.entity.ScreenTimeLogJpaEntity
import org.springframework.data.jpa.repository.JpaRepository

interface ScreenTimeLogJpaRepository : JpaRepository<ScreenTimeLogJpaEntity, Long>
