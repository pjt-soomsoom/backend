package com.soomsoom.backend.domain.activity.model

import com.soomsoom.backend.domain.activity.model.enums.ActivityCategory
import com.soomsoom.backend.domain.activity.model.enums.ActivityType
import java.time.LocalDateTime

class MeditationActivity(
    id: Long?,
    title: String,
    descriptions: List<String>,
    authorId: Long,
    narratorId: Long,
    durationInSeconds: Int,
    thumbnailImageUrl: String?,
    thumbnailFileKey: String?,
    category: ActivityCategory,
    audioUrl: String?,
    audioFileKey: String?,
    miniThumbnailImageUrl: String?,
    miniThumbnailFileKey: String?,
    completionEffectTexts: List<String>,

    createdAt: LocalDateTime? = null,
    modifiedAt: LocalDateTime? = null,
    deletedAt: LocalDateTime? = null,
) : Activity(
    id = id,
    title = title,
    descriptions = descriptions,
    authorId = authorId,
    narratorId = narratorId,
    durationInSeconds = durationInSeconds,
    thumbnailImageUrl = thumbnailImageUrl,
    thumbnailFileKey = thumbnailFileKey,
    category = category,
    audioUrl = audioUrl,
    audioFileKey = audioFileKey,
    miniThumbnailImageUrl = miniThumbnailImageUrl,
    miniThumbnailFileKey = miniThumbnailFileKey,
    completionEffectTexts = completionEffectTexts,
    createdAt = createdAt,
    modifiedAt = modifiedAt,
    deletedAt = deletedAt
) {
    override val type: ActivityType = ActivityType.MEDITATION
}
