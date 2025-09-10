package com.soomsoom.backend.domain.activity.model

import com.soomsoom.backend.domain.activity.model.enums.ActivityCategory
import com.soomsoom.backend.domain.activity.model.enums.ActivityType
import java.time.LocalDateTime

class BreathingActivity(
    id: Long?,
    title: String,
    descriptions: List<String>,
    category: ActivityCategory,
    authorId: Long,
    narratorId: Long,
    durationInSeconds: Int,
    thumbnailImageUrl: String?,
    thumbnailFileKey: String?,
    audioUrl: String?,
    audioFileKey: String?,
    var timeline: List<TimelineEvent>,
    createdAt: LocalDateTime? = null,
    modifiedAt: LocalDateTime? = null,
    deletedAt: LocalDateTime? = null,
) : Activity(
    id = id,
    title = title,
    descriptions = descriptions,
    category = category,
    authorId = authorId,
    narratorId = narratorId,
    durationInSeconds = durationInSeconds,
    thumbnailImageUrl = thumbnailImageUrl,
    thumbnailFileKey = thumbnailFileKey,
    audioUrl = audioUrl,
    audioFileKey = audioFileKey,
    createdAt = createdAt,
    modifiedAt = modifiedAt,
    deletedAt = deletedAt
) {
    override val type: ActivityType = ActivityType.BREATHING
    fun updateTimeline(timeline: List<TimelineEvent>) {
        this.timeline = timeline
    }
}
