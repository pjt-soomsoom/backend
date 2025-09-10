package com.soomsoom.backend.application.port.`in`.item.command.collection

data class CompleteCollectionLottieUpdateCommand(
    val collectionId: Long,
    val lottieFileKey: String?,
)
