package com.soomsoom.backend.domain.activity.model

enum class ActivityType {
    BREATHING, MEDITATION
}

data class Activity(
    val id: Long,
    val instructorId: Long?,
    val name: String,
    val description: String?,
    val type: ActivityType,
    val thumbnailImageUrl: String?,
    val videoUrl: String?,
    val audioUrl: String?,
    val animationUrl: String?,
    val totalDurationSeconds: Long,
) {
}
