package com.soomsoom.backend.application.port.`in`.user.usecase.command

import com.soomsoom.backend.application.port.`in`.user.command.RemoveItemFromCartCommand
import com.soomsoom.backend.application.port.`in`.user.dto.CartDto

interface RemoveItemFromCartUseCase {
    fun removeItemFromCart(command: RemoveItemFromCartCommand): CartDto
}
