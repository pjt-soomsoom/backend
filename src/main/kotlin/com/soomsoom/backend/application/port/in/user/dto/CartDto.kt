package com.soomsoom.backend.application.port.`in`.user.dto

import com.soomsoom.backend.application.port.`in`.item.dto.ItemDto
import com.soomsoom.backend.application.port.`in`.item.dto.toDto
import com.soomsoom.backend.domain.item.model.aggregate.Item
import com.soomsoom.backend.domain.user.model.aggregate.Cart
import com.soomsoom.backend.domain.user.model.aggregate.User

data class CartDto(
    val items: List<ItemDto>,
    val totalPrice: Int,
)

fun Cart.toDto(user: User, items: List<Item>): CartDto {
    val itemDtoList = items.map { it.toDto(user) }
    return CartDto(
        items = itemDtoList,
        totalPrice = itemDtoList.sumOf { it.price }
    )
}
