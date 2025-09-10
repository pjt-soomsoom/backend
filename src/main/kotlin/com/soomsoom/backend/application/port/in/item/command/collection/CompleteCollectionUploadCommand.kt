package com.soomsoom.backend.application.port.`in`.item.command.collection

data class CompleteCollectionUploadCommand(
    val collectionId: Long,
    val imageFileKey: String,
    val lottieFileKey: String?,
)
