package com.soomsoom.backend.application.port.`in`.item.command

import com.soomsoom.backend.application.port.`in`.upload.command.ValidatedFileMetadata

data class UpdateItemImageCommand(
    val itemId: Long,
    val imageMetadata: ValidatedFileMetadata,
)
