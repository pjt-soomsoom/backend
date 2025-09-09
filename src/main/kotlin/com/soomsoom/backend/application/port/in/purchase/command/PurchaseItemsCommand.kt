package com.soomsoom.backend.application.port.`in`.purchase.command

data class PurchaseItemsCommand(
    val userId: Long,
    val itemIds: List<Long>,
    val expectedTotalPrice: Int,
)
