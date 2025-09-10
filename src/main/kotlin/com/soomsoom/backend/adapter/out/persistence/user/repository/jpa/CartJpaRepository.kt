package com.soomsoom.backend.adapter.out.persistence.user.repository.jpa

import com.soomsoom.backend.adapter.out.persistence.user.repository.jpa.entity.CartJpaEntity
import org.springframework.data.jpa.repository.JpaRepository

interface CartJpaRepository : JpaRepository<CartJpaEntity, Long> {
    fun findByUserId(userId: Long): CartJpaEntity?
}
