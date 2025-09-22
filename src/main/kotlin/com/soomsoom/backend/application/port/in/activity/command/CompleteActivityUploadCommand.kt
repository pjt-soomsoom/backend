package com.soomsoom.backend.application.port.`in`.activity.command

class CompleteActivityUploadCommand(
    val activityId: Long,
    val thumbnailFileKey: String,
    val audioFileKey: String,
    val miniThumbnailFileKey: String?,
)
