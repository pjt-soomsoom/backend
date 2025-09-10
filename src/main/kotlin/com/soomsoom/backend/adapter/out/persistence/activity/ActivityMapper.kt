package com.soomsoom.backend.adapter.out.persistence.activity

import com.soomsoom.backend.adapter.out.persistence.activity.repository.jpa.entity.ActivityJpaEntity
import com.soomsoom.backend.adapter.out.persistence.activity.repository.jpa.entity.BreathingActivityJpaEntity
import com.soomsoom.backend.adapter.out.persistence.activity.repository.jpa.entity.MeditationActivityJpaEntity
import com.soomsoom.backend.adapter.out.persistence.activity.repository.jpa.entity.SoundEffectActivityJpaEntity
import com.soomsoom.backend.adapter.out.persistence.activity.repository.jpa.entity.TimelineEventJpaEntity
import com.soomsoom.backend.common.exception.SoomSoomException
import com.soomsoom.backend.domain.activity.ActivityErrorCode
import com.soomsoom.backend.domain.activity.model.Activity
import com.soomsoom.backend.domain.activity.model.BreathingActivity
import com.soomsoom.backend.domain.activity.model.MeditationActivity
import com.soomsoom.backend.domain.activity.model.SoundEffectActivity
import com.soomsoom.backend.domain.activity.model.TimelineEvent

// JPA Entity -> Domain Model
fun ActivityJpaEntity.toDomain(): Activity {
    return when (this) {
        is BreathingActivityJpaEntity -> BreathingActivity(
            id = this.id, title = this.title, descriptions = this.descriptions,
            authorId = this.authorId, narratorId = this.narratorId,
            durationInSeconds = this.durationInSeconds,
            thumbnailImageUrl = this.thumbnailImageUrl, thumbnailFileKey = this.thumbnailFileKey,
            audioUrl = this.audioUrl, audioFileKey = this.audioFileKey,
            timeline = this.timeline.map { it.toDomain() },
            createdAt = this.createdAt, modifiedAt = this.modifiedAt, deletedAt = this.deletedAt, category = this.category
        )
        is MeditationActivityJpaEntity -> MeditationActivity(
            id = this.id, title = this.title, descriptions = this.descriptions,
            authorId = this.authorId, narratorId = this.narratorId,
            durationInSeconds = this.durationInSeconds,
            thumbnailImageUrl = this.thumbnailImageUrl, thumbnailFileKey = this.thumbnailFileKey,
            audioUrl = this.audioUrl, audioFileKey = this.audioFileKey,
            createdAt = this.createdAt, modifiedAt = this.modifiedAt, deletedAt = this.deletedAt, category = this.category
        )
        is SoundEffectActivityJpaEntity -> SoundEffectActivity(
            id = this.id, title = this.title, descriptions = this.descriptions,
            authorId = this.authorId, narratorId = this.narratorId,
            durationInSeconds = this.durationInSeconds,
            thumbnailImageUrl = this.thumbnailImageUrl, thumbnailFileKey = this.thumbnailFileKey,
            audioUrl = this.audioUrl, audioFileKey = this.audioFileKey,
            createdAt = this.createdAt, modifiedAt = this.modifiedAt, deletedAt = this.deletedAt, category = this.category
        )
        else -> throw SoomSoomException(ActivityErrorCode.UNSUPPORTED_ACTIVITY_TYPE)
    }
}

fun TimelineEventJpaEntity.toDomain(): TimelineEvent {
    return TimelineEvent(this.id, this.time, this.action, this.text, this.duration)
}

// Domain Model -> JPA Entity
fun Activity.toJpaEntity(): ActivityJpaEntity {
    return when (this) {
        is BreathingActivity -> BreathingActivityJpaEntity(
            title = this.title,
            descriptions = this.descriptions.toMutableList(),
            authorId = this.authorId,
            narratorId = this.narratorId,
            durationInSeconds = this.durationInSeconds,
            thumbnailImageUrl = this.thumbnailImageUrl,
            audioUrl = this.audioUrl,
            timeline = this.timeline.map { TimelineEventJpaEntity.from(it) }.toMutableList(),
            category = this.category,
            audioFileKey = this.audioFileKey,
            thumbnailFileKey = this.thumbnailFileKey
        ).apply {
            this.thumbnailFileKey = this@toJpaEntity.thumbnailFileKey
            this.audioFileKey = this@toJpaEntity.audioFileKey
        }
        is MeditationActivity -> MeditationActivityJpaEntity(
            title = this.title,
            descriptions = this.descriptions.toMutableList(),
            authorId = this.authorId,
            narratorId = this.narratorId,
            durationInSeconds = this.durationInSeconds,
            thumbnailImageUrl = this.thumbnailImageUrl,
            audioUrl = this.audioUrl,
            category = this.category,
            audioFileKey = this.audioFileKey,
            thumbnailFileKey = this.thumbnailFileKey
        ).apply {
            this.thumbnailFileKey = this@toJpaEntity.thumbnailFileKey
            this.audioFileKey = this@toJpaEntity.audioFileKey
        }
        is SoundEffectActivity -> SoundEffectActivityJpaEntity(
            title = this.title,
            descriptions = this.descriptions.toMutableList(),
            authorId = this.authorId,
            narratorId = this.narratorId,
            durationInSeconds = this.durationInSeconds,
            thumbnailImageUrl = this.thumbnailImageUrl,
            audioUrl = this.audioUrl,
            category = this.category,
            audioFileKey = this.audioFileKey,
            thumbnailFileKey = this.thumbnailFileKey
        ).apply {
            this.thumbnailFileKey = this@toJpaEntity.thumbnailFileKey
            this.audioFileKey = this@toJpaEntity.audioFileKey
        }
        else -> throw SoomSoomException(ActivityErrorCode.UNSUPPORTED_ACTIVITY_TYPE)
    }
}
