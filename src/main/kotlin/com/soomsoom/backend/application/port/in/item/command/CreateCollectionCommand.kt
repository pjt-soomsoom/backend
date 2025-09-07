package com.soomsoom.backend.application.port.`in`.item.command

data class CreateCollectionCommand(
    val name: String,
    val description: String?,
    val phrase: String?,
    val itemIds: List<Long>,
)
