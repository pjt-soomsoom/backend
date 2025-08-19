package com.soomsoom.backend.domain.activity.model

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
    audioUrl: String?,
    audioFileKey: String?,
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
    audioUrl = audioUrl,
    audioFileKey = audioFileKey,
    createdAt = createdAt,
    modifiedAt = modifiedAt,
    deletedAt = deletedAt
)
