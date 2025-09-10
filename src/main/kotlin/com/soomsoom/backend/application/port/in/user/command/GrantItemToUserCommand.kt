package com.soomsoom.backend.application.port.`in`.user.command

data class GrantItemToUserCommand(
    val userId: Long,
    val itemId: Long,
)
