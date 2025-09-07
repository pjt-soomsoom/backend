package com.soomsoom.backend.application.service.user.command

import com.soomsoom.backend.application.port.`in`.user.command.AddItemsToCartCommand
import com.soomsoom.backend.application.port.`in`.user.command.RemoveItemFromCartCommand
import com.soomsoom.backend.application.port.`in`.user.usecase.command.AddItemsToCartUseCase
import com.soomsoom.backend.application.port.`in`.user.usecase.command.RemoveItemFromCartUseCase
import com.soomsoom.backend.application.port.out.user.CartPort
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.transaction.annotation.Transactional

@Transactional
class CartService(
    private val cartPort: CartPort,
) : AddItemsToCartUseCase, RemoveItemFromCartUseCase {

    @PreAuthorize("#command.userId == authentication.principal.id")
    override fun addItemsToCart(command: AddItemsToCartCommand) {
        val cart = cartPort.findByUserId(command.userId)
        command.itemIds.forEach { cart.addItem(it) }
        cartPort.save(cart)
    }

    @PreAuthorize("#command.userId == authentication.principal.id")
    override fun removeItemFromCart(command: RemoveItemFromCartCommand) {
        val cart = cartPort.findByUserId(command.userId)
        cart.removeItem(command.itemId)
        cartPort.save(cart)
    }
}
