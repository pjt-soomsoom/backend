package com.soomsoom.backend.application.port.`in`.user.usecase.command

import com.soomsoom.backend.application.port.`in`.user.command.RemoveItemFromCartCommand

interface RemoveItemFromCartUseCase {
    fun removeItemFromCart(command: RemoveItemFromCartCommand)
}
