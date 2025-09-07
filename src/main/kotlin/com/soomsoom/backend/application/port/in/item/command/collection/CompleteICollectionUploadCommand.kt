package com.soomsoom.backend.application.port.`in`.item.command.collection

data class CompleteICollectionUploadCommand(
    val collectionId: Long,
    val imageFileKey: String,
    val lottieFileKey: String?,
)
