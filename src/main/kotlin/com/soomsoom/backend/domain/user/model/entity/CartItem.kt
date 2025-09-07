package com.soomsoom.backend.domain.user.model.entity

import java.time.LocalDateTime

class CartItem(
    val id: Long = 0L,
    val cartId: Long,
    val itemId: Long,
    val addedAt: LocalDateTime = LocalDateTime.now()
)
