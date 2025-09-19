package com.soomsoom.backend.adapter.out.persistence.rewardedad.repository.jpa

import com.querydsl.jpa.JPAExpressions
import com.querydsl.jpa.impl.JPAQueryFactory
import com.soomsoom.backend.adapter.out.persistence.adrewardlog.repository.jpa.entity.QAdRewardLogJpaEntity.adRewardLogJpaEntity
import com.soomsoom.backend.adapter.out.persistence.rewardedad.repository.jpa.dto.QRewardedAdWithWatchedStatusProjection
import com.soomsoom.backend.adapter.out.persistence.rewardedad.repository.jpa.dto.RewardedAdWithWatchedStatusProjection
import com.soomsoom.backend.adapter.out.persistence.rewardedad.repository.jpa.entity.QRewardedAdJpaEntity.rewardedAdJpaEntity
import org.springframework.stereotype.Repository
import java.time.LocalDateTime

@Repository
class RewardedAdQueryDslRepository(
    private val queryFactory: JPAQueryFactory,
) {
    fun findActiveAdsWithWatchedStatus(userId: Long, start: LocalDateTime, end: LocalDateTime): List<RewardedAdWithWatchedStatusProjection> {
        val watchedAdUnitIdsSubQuery = JPAExpressions
            .select(adRewardLogJpaEntity.adUnitId)
            .from(adRewardLogJpaEntity)
            .where(
                adRewardLogJpaEntity.userId.eq(userId),
                adRewardLogJpaEntity.createdAt.between(start, end)
            )

        return queryFactory
            .select(
                QRewardedAdWithWatchedStatusProjection(
                    rewardedAdJpaEntity.id,
                    rewardedAdJpaEntity.title,
                    rewardedAdJpaEntity.adUnitId,
                    rewardedAdJpaEntity.rewardAmount.value,
                    // adUnitId가 서브쿼리 결과에 포함되어 있는지 확인
                    rewardedAdJpaEntity.adUnitId.`in`(watchedAdUnitIdsSubQuery)
                )
            )
            .from(rewardedAdJpaEntity)
            .where(rewardedAdJpaEntity.active.isTrue)
            .orderBy(rewardedAdJpaEntity.id.asc())
            .fetch()
    }
}
