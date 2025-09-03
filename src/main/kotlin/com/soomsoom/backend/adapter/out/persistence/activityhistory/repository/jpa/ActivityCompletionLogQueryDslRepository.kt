package com.soomsoom.backend.adapter.out.persistence.activityhistory.repository.jpa

import com.querydsl.core.types.dsl.BooleanExpression
import com.querydsl.jpa.impl.JPAQueryFactory
import com.soomsoom.backend.adapter.out.persistence.activity.repository.jpa.entity.BreathingActivityJpaEntity
import com.soomsoom.backend.adapter.out.persistence.activity.repository.jpa.entity.MeditationActivityJpaEntity
import com.soomsoom.backend.adapter.out.persistence.activity.repository.jpa.entity.QActivityJpaEntity.activityJpaEntity
import com.soomsoom.backend.adapter.out.persistence.activityhistory.repository.jpa.entity.QActivityCompletionLogJpaEntity.activityCompletionLogJpaEntity
import com.soomsoom.backend.domain.activity.model.ActivityType
import org.springframework.stereotype.Repository
import java.time.LocalDate

@Repository
class ActivityCompletionLogQueryDslRepository(
    private val queryFactory: JPAQueryFactory,
) {
    fun findLatestCompletionLog(userId: Long, activityType: ActivityType, targetDate: LocalDate) = queryFactory
        .selectFrom(activityCompletionLogJpaEntity)
        .join(activityJpaEntity).on(activityCompletionLogJpaEntity.activityId.eq(activityJpaEntity.id))
        .where(
            activityCompletionLogJpaEntity.userId.eq(userId),
            activityTypeEq(activityType),
            activityCompletionLogJpaEntity.createdAt.lt(targetDate.atStartOfDay())
        )
        .orderBy(activityCompletionLogJpaEntity.createdAt.desc())
        .fetchFirst()

    fun countMonthlyCompletion(userId: Long, activityType: ActivityType, from: LocalDate, to: LocalDate): Long = queryFactory
        .select(activityCompletionLogJpaEntity.count())
        .from(activityCompletionLogJpaEntity)
        .join(activityJpaEntity).on(activityCompletionLogJpaEntity.activityId.eq(activityJpaEntity.id))
        .where(
            activityCompletionLogJpaEntity.userId.eq(userId),
            activityTypeEq(activityType),
            activityCompletionLogJpaEntity.createdAt.between(from.atStartOfDay(), to.plusDays(1).atStartOfDay())
        )
        .fetchOne() ?: 0L

    fun countDistinctActivity(userId: Long, activityType: ActivityType): Long = queryFactory
        .select(activityCompletionLogJpaEntity.activityId.countDistinct())
        .from(activityCompletionLogJpaEntity)
        .join(activityJpaEntity).on(activityCompletionLogJpaEntity.activityId.eq(activityJpaEntity.id))
        .where(
            activityCompletionLogJpaEntity.userId.eq(userId),
            activityTypeEq(activityType)
        )
        .fetchOne() ?: 0L

    private fun activityTypeEq(activityType: ActivityType): BooleanExpression {
        return when (activityType) {
            ActivityType.BREATHING -> activityJpaEntity.instanceOf(BreathingActivityJpaEntity::class.java)
            ActivityType.MEDITATION -> activityJpaEntity.instanceOf(MeditationActivityJpaEntity::class.java)
        }
    }
}
