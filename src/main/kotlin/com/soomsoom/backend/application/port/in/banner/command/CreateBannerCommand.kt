package com.soomsoom.backend.application.port.`in`.banner.command

import com.soomsoom.backend.application.port.`in`.upload.command.ValidatedFileMetadata

data class CreateBannerCommand(
    val description: String,
    val buttonText: String,
    val linkedActivityId: Long,
    val displayOrder: Int,
    val imageMetadata: ValidatedFileMetadata,
)
