package com.soomsoom.backend.application.port.`in`.item.command.item

data class CompleteItemUploadCommand(
    val itemId: Long,
    val imageFileKey: String,
    val lottieFileKey: String?,
)
