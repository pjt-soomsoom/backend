package com.soomsoom.backend.adapter.out.persistence.user.repository.jpa

import com.soomsoom.backend.adapter.out.persistence.user.repository.jpa.entity.UserJpaEntity
import org.springframework.data.jpa.repository.JpaRepository

interface UserJpaRepository : JpaRepository<UserJpaEntity, Long> {
    fun findByDeviceId(deviceId: String): UserJpaEntity?
    fun findByUsername(username: String): UserJpaEntity?
}
