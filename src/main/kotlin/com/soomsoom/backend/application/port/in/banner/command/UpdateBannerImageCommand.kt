package com.soomsoom.backend.application.port.`in`.banner.command

import com.soomsoom.backend.application.port.`in`.upload.command.ValidatedFileMetadata

data class UpdateBannerImageCommand(
    val bannerId: Long,
    val imageMetadata: ValidatedFileMetadata,
)
