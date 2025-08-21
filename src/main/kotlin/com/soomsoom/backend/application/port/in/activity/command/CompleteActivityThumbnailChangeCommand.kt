package com.soomsoom.backend.application.port.`in`.activity.command

data class CompleteActivityThumbnailChangeCommand(
    val userId: Long,
    val activityId: Long,
    val fileKey: String,
)
