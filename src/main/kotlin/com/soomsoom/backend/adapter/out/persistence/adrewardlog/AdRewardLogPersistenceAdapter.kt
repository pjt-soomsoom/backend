package com.soomsoom.backend.adapter.out.persistence.adrewardlog

import com.soomsoom.backend.adapter.out.persistence.adrewardlog.repository.jpa.AdRewardLogJpaRepository
import com.soomsoom.backend.application.port.out.adrewardlog.AdRewardLogPort
import com.soomsoom.backend.domain.adrewardlog.model.AdRewardLog
import org.springframework.stereotype.Component
import java.time.LocalDateTime

@Component
class AdRewardLogPersistenceAdapter(
    private val adRewardLogJpaRepository: AdRewardLogJpaRepository,
) : AdRewardLogPort {
    override fun existsByTransactionId(transactionId: String): Boolean = adRewardLogJpaRepository.existsByTransactionId(transactionId)

    override fun existsByUserIdAndAdUnitIdAndCreatedAtBetween(userId: Long, adUnitId: String, start: LocalDateTime, end: LocalDateTime): Boolean {
        return adRewardLogJpaRepository.existsByUserIdAndAdUnitIdAndCreatedAtBetween(userId, adUnitId, start, end)
    }

    override fun save(adRewardLog: AdRewardLog): AdRewardLog = adRewardLogJpaRepository.save(adRewardLog.toEntity()).toDomain()
}
