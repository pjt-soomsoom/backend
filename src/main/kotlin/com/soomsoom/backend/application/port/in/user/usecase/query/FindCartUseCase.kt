package com.soomsoom.backend.application.port.`in`.user.usecase.query

import com.soomsoom.backend.application.port.`in`.user.dto.CartDto

interface FindCartUseCase {
    fun findCart(userId: Long): CartDto
}
