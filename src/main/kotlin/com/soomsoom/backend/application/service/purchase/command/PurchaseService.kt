package com.soomsoom.backend.application.service.purchase.command

import com.soomsoom.backend.application.port.`in`.item.dto.toDto
import com.soomsoom.backend.application.port.`in`.purchase.command.PurchaseCartItemsCommand
import com.soomsoom.backend.application.port.`in`.purchase.command.PurchaseItemsCommand
import com.soomsoom.backend.application.port.`in`.purchase.dto.PurchaseResultDto
import com.soomsoom.backend.application.port.`in`.purchase.usecase.PurchaseUseCase
import com.soomsoom.backend.application.port.out.item.ItemPort
import com.soomsoom.backend.application.port.out.purchase.PurchaseLogPort
import com.soomsoom.backend.application.port.out.user.CartPort
import com.soomsoom.backend.application.port.out.user.UserPort
import com.soomsoom.backend.common.event.Event
import com.soomsoom.backend.common.event.EventType
import com.soomsoom.backend.common.event.payload.ItemPurchasedPayload
import com.soomsoom.backend.common.exception.SoomSoomException
import com.soomsoom.backend.domain.common.vo.Points
import com.soomsoom.backend.domain.item.ItemErrorCode
import com.soomsoom.backend.domain.item.model.aggregate.Item
import com.soomsoom.backend.domain.purchase.PurchaseErrorCode
import com.soomsoom.backend.domain.purchase.model.aggregate.PurchaseLog
import com.soomsoom.backend.domain.user.UserErrorCode
import com.soomsoom.backend.domain.user.model.aggregate.User
import org.springframework.context.ApplicationEventPublisher
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Transactional
@Service
class PurchaseService(
    private val userPort: UserPort,
    private val itemPort: ItemPort,
    private val cartPort: CartPort,
    private val purchaseLogPort: PurchaseLogPort,
    private val eventPublisher: ApplicationEventPublisher,
) : PurchaseUseCase {

    @PreAuthorize("#command.userId == authentication.principal.id")
    override fun purchaseItems(command: PurchaseItemsCommand): PurchaseResultDto {
        val user = userPort.findById(command.userId)
            ?: throw SoomSoomException(UserErrorCode.NOT_FOUND)

        val itemsToPurchase = itemPort.findAllByIdsForUpdate(command.itemIds)

        if (itemsToPurchase.size != command.itemIds.toSet().size) {
            throw SoomSoomException(ItemErrorCode.NOT_FOUND)
        }

        val purchasedItems = purchaseItemsInternal(user, itemsToPurchase, command.expectedTotalPrice)

        return PurchaseResultDto(
            purchasedItems = purchasedItems.map { it.toDto(user) },
            remainingPoints = user.points.value
        )
    }

    @PreAuthorize("#command.userId == authentication.principal.id")
    override fun purchaseCartItems(command: PurchaseCartItemsCommand): PurchaseResultDto {
        val user = userPort.findById(command.userId)
            ?: throw SoomSoomException(UserErrorCode.NOT_FOUND)

        val cart = cartPort.findByUserId(command.userId)
        if (cart.items.isEmpty()) {
            return PurchaseResultDto(emptyList(), user.points.value)
        }

        val itemsToPurchase = itemPort.findAllByIdsForUpdate(cart.items.map { it.itemId })
        val purchasedItems = purchaseItemsInternal(user, itemsToPurchase, command.expectedTotalPrice)

        cart.clear()
        cartPort.save(cart)

        return PurchaseResultDto(
            purchasedItems = purchasedItems.map { it.toDto(user) },
            remainingPoints = user.points.value
        )
    }

    private fun purchaseItemsInternal(user: User, items: List<Item>, expectedTotalPrice: Int): List<Item> {
        val actualTotalPrice = Points(items.sumOf { it.price.value })

        check(actualTotalPrice.value == expectedTotalPrice) {
            throw SoomSoomException(PurchaseErrorCode.PRICE_MISMATCH)
        }

        user.deductPoints(actualTotalPrice)

        val purchasedItems = items.onEach { item ->
            item.validatePurchasable()
            item.recordSale()
            user.ownItem(item.id)

            val log = PurchaseLog.record(user.id!!, item.id, item.price, item.acquisitionType)
            purchaseLogPort.save(log)

            eventPublisher.publishEvent(
                Event(
                    eventType = EventType.ITEM_PURCHASED,
                    payload = ItemPurchasedPayload(
                        userId = user.id,
                        itemId = item.id,
                        acquisitionType = item.acquisitionType
                    )
                )
            )
        }

        userPort.save(user)
        purchasedItems.forEach { itemPort.save(it) }

        return purchasedItems
    }
}
