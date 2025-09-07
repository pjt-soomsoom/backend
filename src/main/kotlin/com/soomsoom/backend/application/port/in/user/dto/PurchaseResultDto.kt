package com.soomsoom.backend.application.port.`in`.user.dto

data class PurchaseResultDto(
    val purchasedItems: List<OwnedItemDto>,
    val remainingPoints: Int,
)
