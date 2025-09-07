package com.soomsoom.backend.application.port.`in`.user.command

data class RemoveItemFromCartCommand(
    val userId: Long,
    val itemId: Long,
)
