package com.soomsoom.backend.application.port.`in`.user.command

data class GrantItemsToUserCommand(
    val userId: Long,
    val itemIds: List<Long>,
)
