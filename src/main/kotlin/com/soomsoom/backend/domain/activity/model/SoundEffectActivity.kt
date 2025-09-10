package com.soomsoom.backend.domain.activity.model

import com.soomsoom.backend.domain.activity.model.enums.ActivityCategory
import com.soomsoom.backend.domain.activity.model.enums.ActivityType
import java.time.LocalDateTime

class SoundEffectActivity(
    id: Long? = null,
    title: String,
    thumbnailImageUrl: String?,
    thumbnailFileKey: String?,
    descriptions: List<String>,
    category: ActivityCategory,
    authorId: Long,
    narratorId: Long,
    durationInSeconds: Int,
    audioUrl: String?,
    audioFileKey: String?,
    createdAt: LocalDateTime? = null,
    modifiedAt: LocalDateTime? = null,
    deletedAt: LocalDateTime? = null,
) : Activity(
    id = id,
    title = title,
    thumbnailImageUrl = thumbnailImageUrl,
    thumbnailFileKey = thumbnailFileKey,
    descriptions = descriptions,
    category = category,
    authorId = authorId,
    narratorId = narratorId,
    durationInSeconds = durationInSeconds,
    audioUrl = audioUrl,
    audioFileKey = audioFileKey,
    createdAt = createdAt,
    modifiedAt = modifiedAt,
    deletedAt = deletedAt
) {
    override val type: ActivityType = ActivityType.SOUND_EFFECT
}
