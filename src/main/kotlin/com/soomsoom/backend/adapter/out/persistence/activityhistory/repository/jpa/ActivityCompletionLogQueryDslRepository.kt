package com.soomsoom.backend.adapter.out.persistence.activityhistory.repository.jpa

import com.querydsl.jpa.impl.JPAQueryFactory
import com.soomsoom.backend.adapter.out.persistence.activityhistory.repository.jpa.entity.QActivityCompletionLogJpaEntity.activityCompletionLogJpaEntity
import com.soomsoom.backend.domain.activity.model.enums.ActivityType
import org.springframework.stereotype.Repository
import java.time.LocalDate
import java.time.LocalDateTime

@Repository
class ActivityCompletionLogQueryDslRepository(
    private val queryFactory: JPAQueryFactory,
) {
    fun findLatestCompletionLog(userId: Long, activityType: ActivityType, targetDate: LocalDate) = queryFactory
        .selectFrom(activityCompletionLogJpaEntity)
        .where(
            activityCompletionLogJpaEntity.userId.eq(userId),
            activityCompletionLogJpaEntity.activityType.eq(activityType),
            activityCompletionLogJpaEntity.createdAt.lt(targetDate.atStartOfDay())
        )
        .orderBy(activityCompletionLogJpaEntity.createdAt.desc())
        .fetchFirst()

    fun countByUserIdAndActivityTypeAndCreatedAtBetween(
        userId: Long,
        activityType: ActivityType,
        from: LocalDateTime,
        to: LocalDateTime,
    ): Long {
        return queryFactory
            .select(activityCompletionLogJpaEntity.count())
            .from(activityCompletionLogJpaEntity)
            .where(
                activityCompletionLogJpaEntity.userId.eq(userId),
                activityCompletionLogJpaEntity.activityType.eq(activityType),
                activityCompletionLogJpaEntity.createdAt.between(from, to)
            )
            .fetchOne() ?: 0L
    }

    fun countDistinctActivity(userId: Long, activityType: ActivityType): Long = queryFactory
        .select(activityCompletionLogJpaEntity.activityId.countDistinct())
        .from(activityCompletionLogJpaEntity)
        .where(
            activityCompletionLogJpaEntity.userId.eq(userId),
            activityCompletionLogJpaEntity.activityType.eq(activityType)
        )
        .fetchOne() ?: 0L

    fun existsByUserIdAndTypesAndCreatedAtBetween(
        userId: Long,
        activityTypes: List<ActivityType>,
        from: LocalDateTime,
        to: LocalDateTime,
    ): Boolean {
        return queryFactory
            .selectOne()
            .from(activityCompletionLogJpaEntity)
            .where(
                activityCompletionLogJpaEntity.userId.eq(userId),
                activityCompletionLogJpaEntity.createdAt.between(from, to),
                activityCompletionLogJpaEntity.activityType.`in`(activityTypes)
            )
            .fetchFirst() != null
    }
}
