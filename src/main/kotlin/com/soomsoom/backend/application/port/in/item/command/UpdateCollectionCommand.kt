package com.soomsoom.backend.application.port.`in`.item.command

data class UpdateCollectionCommand(
    val collectionId: Long,
    val name: String,
    val description: String?,
    val phrase: String?,
    val itemIds: List<Long>,
)
