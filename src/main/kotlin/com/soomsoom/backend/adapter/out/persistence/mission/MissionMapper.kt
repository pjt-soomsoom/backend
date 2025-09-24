package com.soomsoom.backend.adapter.out.persistence.mission

import com.fasterxml.jackson.databind.ObjectMapper
import com.soomsoom.backend.adapter.out.persistence.mission.repository.jpa.entity.MissionCompletionLogJpaEntity
import com.soomsoom.backend.adapter.out.persistence.mission.repository.jpa.entity.MissionJpaEntity
import com.soomsoom.backend.adapter.out.persistence.mission.repository.jpa.entity.MissionRewardEmbeddable
import com.soomsoom.backend.adapter.out.persistence.mission.repository.jpa.entity.NotificationContentEmbeddable
import com.soomsoom.backend.adapter.out.persistence.mission.repository.jpa.entity.UserMissionProgressJpaEntity
import com.soomsoom.backend.domain.mission.model.aggregate.Mission
import com.soomsoom.backend.domain.mission.model.entity.MissionCompletionLog
import com.soomsoom.backend.domain.mission.model.entity.UserMissionProgress
import com.soomsoom.backend.domain.mission.model.vo.MissionProgressData
import com.soomsoom.backend.domain.mission.model.vo.MissionReward
import com.soomsoom.backend.domain.mission.model.vo.NotificationContent

// Mission <-> MissionJpaEntity
fun Mission.toEntity(): MissionJpaEntity = MissionJpaEntity(
    id = this.id,
    type = this.type,
    title = this.title,
    description = this.description,
    targetValue = this.targetValue,
    completionNotification = this.completionNotification.toEmbeddable(),
    reward = this.reward.toEmbeddable(),
    repeatableType = this.repeatableType,
    claimType = this.claimType
).also {
    it.deletedAt = this.deletedAt
}

fun MissionJpaEntity.toDomain(): Mission = Mission(
    id = this.id,
    type = this.type,
    title = this.title,
    description = this.description,
    targetValue = this.targetValue,
    completionNotification = this.completionNotification.toDomain(),
    reward = this.reward.toDomain(),
    repeatableType = this.repeatableType,
    claimType = this.claimType,
    createdAt = this.createdAt,
    modifiedAt = this.modifiedAt,
    deletedAt = this.deletedAt
)

// MissionReward VO <-> MissionRewardEmbeddable
fun MissionReward.toEmbeddable(): MissionRewardEmbeddable = MissionRewardEmbeddable(
    points = this.points,
    itemId = this.itemId,
    notification = this.notification.toEmbeddable()
)

fun MissionRewardEmbeddable.toDomain(): MissionReward = MissionReward(
    points = this.points,
    itemId = this.itemId,
    notification = this.notification.toDomain()
)

// NotificationContent VO <-> NotificationContentEmbeddable
fun NotificationContent.toEmbeddable(): NotificationContentEmbeddable = NotificationContentEmbeddable(
    title = this.title,
    body = this.body
)

fun NotificationContentEmbeddable.toDomain(): NotificationContent = NotificationContent(
    title = this.title,
    body = this.body
)

// UserMissionProgress <-> UserMissionProgressJpaEntity
fun UserMissionProgress.toEntity(objectMapper: ObjectMapper): UserMissionProgressJpaEntity = UserMissionProgressJpaEntity(
    id = this.id,
    userId = this.userId,
    missionId = this.missionId,
    progressData = objectMapper.writeValueAsString(this.progressData)
)

fun UserMissionProgressJpaEntity.toDomain(objectMapper: ObjectMapper): UserMissionProgress = UserMissionProgress(
    id = this.id,
    userId = this.userId,
    missionId = this.missionId,
    progressData = objectMapper.readValue(this.progressData, MissionProgressData::class.java),
    updatedAt = this.modifiedAt!!
)

// MissionCompletionLog <-> MissionCompletionLogJpaEntity
fun MissionCompletionLog.toEntity(): MissionCompletionLogJpaEntity = MissionCompletionLogJpaEntity(
    id = this.id,
    userId = this.userId,
    missionId = this.missionId,
    completedAt = this.completedAt,
    rewardedAt = this.rewardedAt
)

fun MissionCompletionLogJpaEntity.toDomain(): MissionCompletionLog = MissionCompletionLog(
    id = this.id,
    userId = this.userId,
    missionId = this.missionId,
    completedAt = this.completedAt,
    rewardedAt = this.rewardedAt
)
