package com.soomsoom.backend.application.port.`in`.user.command

data class PurchaseItemCommand(
    val userId: Long,
    val itemId: Long,
)
