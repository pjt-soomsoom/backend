package com.soomsoom.backend.application.service.user.command

import com.soomsoom.backend.application.port.`in`.user.command.AddItemsToCartCommand
import com.soomsoom.backend.application.port.`in`.user.command.RemoveItemFromCartCommand
import com.soomsoom.backend.application.port.`in`.user.dto.CartDto
import com.soomsoom.backend.application.port.`in`.user.dto.toDto
import com.soomsoom.backend.application.port.`in`.user.usecase.command.AddItemsToCartUseCase
import com.soomsoom.backend.application.port.`in`.user.usecase.command.RemoveItemFromCartUseCase
import com.soomsoom.backend.application.port.out.item.ItemPort
import com.soomsoom.backend.application.port.out.user.CartPort
import com.soomsoom.backend.application.port.out.user.UserPort
import com.soomsoom.backend.common.exception.SoomSoomException
import com.soomsoom.backend.domain.item.ItemErrorCode
import com.soomsoom.backend.domain.user.UserErrorCode
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class CartService(
    private val cartPort: CartPort,
    private val userPort: UserPort,
    private val itemPort: ItemPort,
) : AddItemsToCartUseCase, RemoveItemFromCartUseCase {

    @PreAuthorize("#command.userId == authentication.principal.id")
    override fun addItemsToCart(command: AddItemsToCartCommand): CartDto {
        val user = userPort.findById(command.userId) ?: throw SoomSoomException(UserErrorCode.NOT_FOUND)

        val itemsToAdd = itemPort.findAllByIds(command.itemIds)
        if (itemsToAdd.size != command.itemIds.toSet().size) {
            throw SoomSoomException(ItemErrorCode.NOT_FOUND)
        }

        val cart = cartPort.findByUserId(command.userId)

        itemsToAdd.forEach { cart.addItem(it.id) }

        val savedCart = cartPort.save(cart)
        val itemsInCart = itemPort.findAllByIds(savedCart.items.map { it.itemId })

        return savedCart.toDto(user, itemsInCart)
    }

    @PreAuthorize("#command.userId == authentication.principal.id")
    override fun removeItemFromCart(command: RemoveItemFromCartCommand): CartDto {
        val user = userPort.findById(command.userId) ?: throw SoomSoomException(UserErrorCode.NOT_FOUND)
        val cart = cartPort.findByUserId(command.userId)

        cart.removeItem(command.itemId)
        val savedCart = cartPort.save(cart)

        val itemsInCart = itemPort.findAllByIds(savedCart.items.map { it.itemId })

        return savedCart.toDto(user, itemsInCart)
    }
}
