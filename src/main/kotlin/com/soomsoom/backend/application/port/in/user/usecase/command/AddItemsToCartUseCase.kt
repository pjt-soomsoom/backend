package com.soomsoom.backend.application.port.`in`.user.usecase.command

import com.soomsoom.backend.application.port.`in`.user.command.AddItemsToCartCommand
import com.soomsoom.backend.application.port.`in`.user.dto.CartDto

interface AddItemsToCartUseCase {
    fun addItemsToCart(command: AddItemsToCartCommand): CartDto
}
