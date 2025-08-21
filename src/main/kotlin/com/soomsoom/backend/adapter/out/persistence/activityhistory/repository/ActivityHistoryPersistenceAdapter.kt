package com.soomsoom.backend.adapter.out.persistence.activityhistory.repository

import com.soomsoom.backend.adapter.out.persistence.activityhistory.repository.jpa.ActivityCompletionLogJpaRepository
import com.soomsoom.backend.adapter.out.persistence.activityhistory.repository.jpa.ActivityProgressJpaRepository
import com.soomsoom.backend.adapter.out.persistence.activityhistory.repository.jpa.entity.UserActivitySummaryJpaRepository
import com.soomsoom.backend.adapter.out.persistence.activityhistory.toDomain
import com.soomsoom.backend.adapter.out.persistence.activityhistory.toEntity
import com.soomsoom.backend.application.port.out.activityhistory.ActivityHistoryPort
import com.soomsoom.backend.domain.activityhistory.model.ActivityCompletionLog
import com.soomsoom.backend.domain.activityhistory.model.ActivityProgress
import com.soomsoom.backend.domain.activityhistory.model.UserActivitySummary
import org.springframework.stereotype.Component

@Component
class ActivityHistoryPersistenceAdapter(
    private val activityProgressJpaRepository: ActivityProgressJpaRepository,
    private val activityCompletionLogJpaRepository: ActivityCompletionLogJpaRepository,
    private val userActivitySummaryJpaRepository: UserActivitySummaryJpaRepository,
) : ActivityHistoryPort {

    override fun findProgress(userId: Long, activityId: Long): ActivityProgress? {
        return activityProgressJpaRepository.findByUserIdAndActivityId(userId, activityId)?.toDomain()
    }

    override fun saveProgress(progress: ActivityProgress): ActivityProgress {
        val entity = progress.toEntity()
        return activityProgressJpaRepository.save(entity).toDomain()
    }

    override fun saveCompletionLog(log: ActivityCompletionLog): ActivityCompletionLog {
        val entity = log.toEntity()
        return activityCompletionLogJpaRepository.save(entity).toDomain()
    }

    override fun findUserSummary(userId: Long): UserActivitySummary? {
        return userActivitySummaryJpaRepository.findByUserId(userId)?.toDomain()
    }

    override fun saveUserSummary(summary: UserActivitySummary): UserActivitySummary {
        val entity = summary.toEntity()
        return userActivitySummaryJpaRepository.save(entity).toDomain()
    }

    override fun countCompletedActivities(userId: Long): Long {
        return activityCompletionLogJpaRepository.countByUserId(userId)
    }
}
