package com.soomsoom.backend.adapter.out.persistence.purchase.repository.jpa

import com.soomsoom.backend.adapter.out.persistence.purchase.repository.jpa.entity.PurchaseLogJpaEntity
import org.springframework.data.jpa.repository.JpaRepository

interface PurchaseLogJpaRepository : JpaRepository<PurchaseLogJpaEntity, Long> {
    fun deleteAllByUserId(userId: Long)
}
