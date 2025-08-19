package com.soomsoom.backend.application.port.`in`.activity.command

data class CompleteActivityThumbnailChangeCommand(
    val activityId: Long,
    val fileKey: String,
)
