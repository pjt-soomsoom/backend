package com.soomsoom.backend.application.port.`in`.item.command.item

data class CompleteItemImageUpdateCommand(
    val itemId: Long,
    val imageFileKey: String,
)
