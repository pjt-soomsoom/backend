package com.soomsoom.backend.application.port.`in`.purchase.command

data class PurchaseItemCommand(
    val userId: Long,
    val itemId: Long,
)
