package com.soomsoom.backend.adapter.out.persistence.activityhistory

import com.soomsoom.backend.adapter.out.persistence.activityhistory.repository.jpa.entity.ActivityCompletionLogJpaEntity
import com.soomsoom.backend.adapter.out.persistence.activityhistory.repository.jpa.entity.ActivityProgressJpaEntity
import com.soomsoom.backend.adapter.out.persistence.activityhistory.repository.jpa.entity.UserActivitySummaryJpaEntity
import com.soomsoom.backend.domain.activityhistory.model.ActivityCompletionLog
import com.soomsoom.backend.domain.activityhistory.model.ActivityProgress
import com.soomsoom.backend.domain.activityhistory.model.UserActivitySummary

// Mappers for ActivityProgress
fun ActivityProgress.toEntity(): ActivityProgressJpaEntity = ActivityProgressJpaEntity(
    id = this.id ?: 0,
    userId = this.userId,
    activityId = this.activityId,
    progressSeconds = this.progressSeconds
)
fun ActivityProgressJpaEntity.toDomain(): ActivityProgress = ActivityProgress(
    id = this.id,
    userId = this.userId,
    activityId = this.activityId,
    progressSeconds = this.progressSeconds,
    createdAt = this.createdAt,
    modifiedAt = this.modifiedAt
)

// Mappers for ActivityCompletionLog
fun ActivityCompletionLog.toEntity(): ActivityCompletionLogJpaEntity = ActivityCompletionLogJpaEntity(
    id = this.id ?: 0,
    userId = this.userId,
    activityId = this.activityId,
    activityType = this.activityType
)
fun ActivityCompletionLogJpaEntity.toDomain(): ActivityCompletionLog = ActivityCompletionLog(
    id = this.id,
    userId = this.userId,
    activityId = this.activityId,
    createdAt = this.createdAt,
    activityType = this.activityType
)

// Mappers for UserActivitySummary
fun UserActivitySummary.toEntity(): UserActivitySummaryJpaEntity = UserActivitySummaryJpaEntity(
    id = this.id ?: 0,
    userId = this.userId,
    totalPlaySeconds = this.totalPlaySeconds
)
fun UserActivitySummaryJpaEntity.toDomain(): UserActivitySummary = UserActivitySummary(
    id = this.id,
    userId = this.userId,
    totalPlaySeconds = this.totalPlaySeconds,
    createdAt = this.createdAt,
    modifiedAt = this.modifiedAt
)
