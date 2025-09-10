package com.soomsoom.backend.application.port.`in`.item.command.collection

data class UpdateCollectionInfoCommand(
    val collectionId: Long,
    val name: String,
    val description: String?,
    val phrase: String?,
    val itemIds: List<Long>,
)
