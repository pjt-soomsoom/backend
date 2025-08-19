package com.soomsoom.backend.application.port.`in`.activity.command

import com.soomsoom.backend.application.port.`in`.upload.command.ValidatedFileMetadata

data class ChangeActivityThumbnailCommand(
    val activityId: Long,
    val thumbnailImageMetadata: ValidatedFileMetadata,
)
