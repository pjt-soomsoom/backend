package com.soomsoom.backend.application.port.`in`.item.command.item

import com.soomsoom.backend.application.port.`in`.upload.command.ValidatedFileMetadata

data class UpdateItemLottieCommand(
    val itemId: Long,
    val lottieMetadata: ValidatedFileMetadata?,
)
