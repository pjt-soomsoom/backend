package com.soomsoom.backend.adapter.out.persistence.adrewardlog.repository.jpa

import com.soomsoom.backend.adapter.out.persistence.adrewardlog.repository.jpa.entity.AdRewardLogJpaEntity
import org.springframework.data.jpa.repository.JpaRepository
import java.time.LocalDateTime

interface AdRewardLogJpaRepository : JpaRepository<AdRewardLogJpaEntity, Long> {
    fun existsByTransactionId(transactionId: String): Boolean
    fun existsByUserIdAndAdUnitIdAndCreatedAtBetween(userId: Long, adUnitId: String, start: LocalDateTime, end: LocalDateTime): Boolean
    fun deleteAllByUserId(userId: Long)
}
