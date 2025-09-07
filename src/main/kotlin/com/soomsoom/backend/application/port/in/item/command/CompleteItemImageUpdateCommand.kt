package com.soomsoom.backend.application.port.`in`.item.command

data class CompleteItemImageUpdateCommand(
    val itemId: Long,
    val imageFileKey: String,
)
