package com.soomsoom.backend.adapter.out.persistence.activityhistory

import com.soomsoom.backend.adapter.out.persistence.activityhistory.repository.jpa.ActivityCompletionLogJpaRepository
import com.soomsoom.backend.adapter.out.persistence.activityhistory.repository.jpa.ActivityCompletionLogQueryDslRepository
import com.soomsoom.backend.adapter.out.persistence.activityhistory.repository.jpa.ActivityProgressJpaRepository
import com.soomsoom.backend.adapter.out.persistence.activityhistory.repository.jpa.UserActivitySummaryJpaRepository
import com.soomsoom.backend.application.port.out.activityhistory.ActivityHistoryPort
import com.soomsoom.backend.domain.activity.model.enums.ActivityType
import com.soomsoom.backend.domain.activityhistory.model.ActivityCompletionLog
import com.soomsoom.backend.domain.activityhistory.model.ActivityProgress
import com.soomsoom.backend.domain.activityhistory.model.UserActivitySummary
import org.springframework.stereotype.Component
import java.time.LocalDate
import java.time.LocalDateTime

@Component
class ActivityHistoryPersistenceAdapter(
    private val activityProgressJpaRepository: ActivityProgressJpaRepository,
    private val activityCompletionLogJpaRepository: ActivityCompletionLogJpaRepository,
    private val userActivitySummaryJpaRepository: UserActivitySummaryJpaRepository,
    private val activityCompletionLogQueryDslRepository: ActivityCompletionLogQueryDslRepository,
) : ActivityHistoryPort {

    override fun findProgress(userId: Long, activityId: Long): ActivityProgress? {
        return activityProgressJpaRepository.findByUserIdAndActivityId(userId, activityId)?.toDomain()
    }

    override fun saveProgress(progress: ActivityProgress): ActivityProgress {
        val entity = progress.toEntity()
        return activityProgressJpaRepository.save(entity).toDomain()
    }

    override fun deleteActivityProgressByUserId(userId: Long) {
        activityProgressJpaRepository.deleteAllByUserId(userId)
    }

    override fun saveCompletionLog(log: ActivityCompletionLog): ActivityCompletionLog {
        val entity = log.toEntity()
        return activityCompletionLogJpaRepository.save(entity).toDomain()
    }

    override fun deleteActivityCompletionLogByUserId(userId: Long) {
        activityCompletionLogJpaRepository.deleteAllByUserId(userId)
    }

    override fun findUserSummary(userId: Long): UserActivitySummary? {
        return userActivitySummaryJpaRepository.findByUserId(userId)?.toDomain()
    }

    override fun saveUserSummary(summary: UserActivitySummary): UserActivitySummary {
        val entity = summary.toEntity()
        return userActivitySummaryJpaRepository.save(entity).toDomain()
    }

    override fun deleteUserActivitySummaryByUserId(userId: Long) {
        userActivitySummaryJpaRepository.deleteAllByUserId(userId)
    }

    override fun countCompletedActivities(userId: Long): Long {
        return activityCompletionLogJpaRepository.countByUserId(userId)
    }

    override fun findLatestCompletionLogBefore(userId: Long, activityType: ActivityType, targetDate: LocalDate): ActivityCompletionLog? {
        return activityCompletionLogQueryDslRepository.findLatestCompletionLog(userId, activityType, targetDate)?.toDomain()
    }

    override fun countCompletionByPeriod(userId: Long, activityType: ActivityType, from: LocalDateTime, to: LocalDateTime): Long {
        return activityCompletionLogQueryDslRepository.countByUserIdAndActivityTypeAndCreatedAtBetween(userId, activityType, from, to)
    }

    override fun countDistinctActivity(userId: Long, activityType: ActivityType): Long {
        return activityCompletionLogQueryDslRepository.countDistinctActivity(userId, activityType)
    }

    override fun existsByUserIdAndTypesAndCreatedAtBetween(
        userId: Long,
        activityTypes: List<ActivityType>,
        from: LocalDateTime,
        to: LocalDateTime,
    ): Boolean {
        return activityCompletionLogQueryDslRepository.existsByUserIdAndTypesAndCreatedAtBetween(
            userId = userId,
            activityTypes = activityTypes,
            from = from,
            to = to
        )
    }
}
