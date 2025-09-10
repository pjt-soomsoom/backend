package com.soomsoom.backend.application.port.`in`.purchase.command

data class PurchaseCartItemsCommand(
    val userId: Long,
    val expectedTotalPrice: Int,
)
