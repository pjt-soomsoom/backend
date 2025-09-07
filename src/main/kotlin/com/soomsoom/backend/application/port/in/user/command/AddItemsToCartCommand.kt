package com.soomsoom.backend.application.port.`in`.user.command

data class AddItemsToCartCommand(
    val userId: Long,
    val itemId: List<Long>,
)
