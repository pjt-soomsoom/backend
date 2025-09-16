package com.soomsoom.backend.adapter.out.persistence.useractive.repository.jpa

import com.soomsoom.backend.adapter.out.persistence.useractive.repository.jpa.entity.ScreenTimeLogJpaEntity
import org.springframework.data.jpa.repository.JpaRepository

interface ScreenTimeLogJpaRepository : JpaRepository<ScreenTimeLogJpaEntity, Long>
