package com.soomsoom.backend.application.port.`in`.user.dto

import com.soomsoom.backend.application.port.`in`.item.dto.ItemDto

data class CartDto(
    val userId: Long,
    val items: List<ItemDto>,
    val totalPrice: Int,
)
