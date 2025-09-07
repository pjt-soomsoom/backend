package com.soomsoom.backend.application.port.out.user

import com.soomsoom.backend.domain.user.model.aggregate.Cart

interface CartPort {
    fun findByUserId(userId: Long): Cart?
    fun save(cart: Cart): Cart
}
