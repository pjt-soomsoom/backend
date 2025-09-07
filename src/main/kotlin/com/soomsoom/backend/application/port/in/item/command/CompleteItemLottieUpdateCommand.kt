package com.soomsoom.backend.application.port.`in`.item.command

data class CompleteItemLottieUpdateCommand(
    val itemId: Long,
    val lottieFileKey: String?,
)
