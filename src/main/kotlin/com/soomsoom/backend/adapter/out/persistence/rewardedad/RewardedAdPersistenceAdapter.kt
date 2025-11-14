package com.soomsoom.backend.adapter.out.persistence.rewardedad

import com.soomsoom.backend.adapter.out.persistence.rewardedad.repository.jpa.RewardedAdJpaRepository
import com.soomsoom.backend.adapter.out.persistence.rewardedad.repository.jpa.RewardedAdQueryDslRepository
import com.soomsoom.backend.application.port.`in`.rewardedad.dto.RewardedAdStatusDto
import com.soomsoom.backend.application.port.out.rewardedad.RewardedAdPort
import com.soomsoom.backend.common.entity.enums.OSType
import com.soomsoom.backend.domain.rewardedad.model.RewardedAd
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Component
import java.time.LocalDateTime

@Component
class RewardedAdPersistenceAdapter(
    private val rewardedAdJpaRepository: RewardedAdJpaRepository,
    private val rewardedAdQueryDslRepository: RewardedAdQueryDslRepository,
) : RewardedAdPort {
    override fun findActiveAdsWithWatchedStatus(
        userId: Long,
        start: LocalDateTime,
        end: LocalDateTime,
        platform: OSType,
    ): List<RewardedAdStatusDto> {
        return rewardedAdQueryDslRepository.findActiveAdsWithWatchedStatus(userId, start, end, platform).map {
            RewardedAdStatusDto(
                id = it.id,
                title = it.title,
                adUnitId = it.adUnitId,
                rewardAmount = it.rewardAmount,
                watchedToday = it.watched,
                platform = it.platform
            )
        }
    }

    override fun findById(id: Long): RewardedAd? = rewardedAdJpaRepository.findByIdOrNull(id)?.toDomain()
    override fun findAll(): List<RewardedAd> = rewardedAdJpaRepository.findAll().map { it.toDomain() }
    override fun save(rewardedAd: RewardedAd): RewardedAd = rewardedAdJpaRepository.save(rewardedAd.toEntity()).toDomain()
    override fun deleteById(id: Long) = rewardedAdJpaRepository.deleteById(id)
}
