package com.soomsoom.backend.adapter.out.persistence.auth.repository.jpa

import com.soomsoom.backend.adapter.out.persistence.auth.repository.jpa.entity.RefreshTokenJpaEntity
import org.springframework.data.jpa.repository.JpaRepository

interface RefreshTokenJpaRepository : JpaRepository<RefreshTokenJpaEntity, String> {
    fun findByUserId(userId: Long): List<RefreshTokenJpaEntity>
    fun deleteAllByUserId(userId: Long)
}
