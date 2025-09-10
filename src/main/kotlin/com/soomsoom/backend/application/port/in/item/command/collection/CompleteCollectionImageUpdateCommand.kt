package com.soomsoom.backend.application.port.`in`.item.command.collection

data class CompleteCollectionImageUpdateCommand(
    val collectionId: Long,
    val imageFileKey: String,
)
