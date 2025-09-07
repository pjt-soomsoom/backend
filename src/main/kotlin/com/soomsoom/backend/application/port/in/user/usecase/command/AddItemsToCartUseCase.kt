package com.soomsoom.backend.application.port.`in`.user.usecase.command

import com.soomsoom.backend.application.port.`in`.user.command.AddItemsToCartCommand

interface AddItemsToCartUseCase {
    fun addItemsToCart(command: AddItemsToCartCommand)
}
