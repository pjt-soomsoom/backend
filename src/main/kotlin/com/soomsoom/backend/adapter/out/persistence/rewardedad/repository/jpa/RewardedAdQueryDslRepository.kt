package com.soomsoom.backend.adapter.out.persistence.rewardedad.repository.jpa

import com.querydsl.jpa.impl.JPAQueryFactory
import com.soomsoom.backend.adapter.out.persistence.adrewardlog.repository.jpa.entity.QAdRewardLogJpaEntity.adRewardLogJpaEntity
import com.soomsoom.backend.adapter.out.persistence.rewardedad.repository.jpa.dto.QRewardedAdWithWatchedStatusProjection
import com.soomsoom.backend.adapter.out.persistence.rewardedad.repository.jpa.dto.RewardedAdWithWatchedStatusProjection
import com.soomsoom.backend.adapter.out.persistence.rewardedad.repository.jpa.entity.QRewardedAdJpaEntity.rewardedAdJpaEntity
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Repository
import java.time.LocalDateTime

@Repository
class RewardedAdQueryDslRepository(
    private val queryFactory: JPAQueryFactory,
) {
    private val logger = LoggerFactory.getLogger(javaClass)
    fun findActiveAdsWithWatchedStatus(userId: Long, start: LocalDateTime, end: LocalDateTime): List<RewardedAdWithWatchedStatusProjection> {
        logger.info("start = {}, end = {}", start, end)
        return queryFactory
            .select(
                QRewardedAdWithWatchedStatusProjection(
                    rewardedAdJpaEntity.id,
                    rewardedAdJpaEntity.title,
                    rewardedAdJpaEntity.adUnitId,
                    rewardedAdJpaEntity.rewardAmount.value,
                    adRewardLogJpaEntity.id.isNotNull()
                )
            )
            .from(rewardedAdJpaEntity)
            .leftJoin(adRewardLogJpaEntity).on(
                rewardedAdJpaEntity.adUnitId.eq(adRewardLogJpaEntity.adUnitId)
                    .and(adRewardLogJpaEntity.userId.eq(userId))
                    .and(adRewardLogJpaEntity.createdAt.between(start, end))
            )
            .where(rewardedAdJpaEntity.active.isTrue)
            .orderBy(rewardedAdJpaEntity.id.asc())
            .fetch()
    }
}
