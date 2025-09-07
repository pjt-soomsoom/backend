package com.soomsoom.backend.application.port.`in`.purchase.dto

import com.soomsoom.backend.application.port.`in`.item.dto.ItemDto

data class PurchaseResultDto(
    val purchasedItems: List<ItemDto>,
    val remainingPoints: Int,
)
