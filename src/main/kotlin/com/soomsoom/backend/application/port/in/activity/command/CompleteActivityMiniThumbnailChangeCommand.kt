package com.soomsoom.backend.application.port.`in`.activity.command

data class CompleteActivityMiniThumbnailChangeCommand(
    val userId: Long,
    val activityId: Long,
    val fileKey: String,
)
