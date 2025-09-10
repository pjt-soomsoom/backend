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
        val user = userPort.findByIdWithCollections(command.userId)
            ?: throw SoomSoomException(UserErrorCode.NOT_FOUND)

        // 구매 가능한지 검증 후 가능하다면 구매할 아이템을 반환
        val itemsToPurchase = validate(command.itemIds, user)

        // 구매 처리 및 구매 완료 이벤트 발행
        val purchasedItems = purchaseItemsInternal(user, itemsToPurchase, command.expectedTotalPrice)

        // 구매 완료한 물품은 장바구니에서 제거
        val cart = cartPort.findByUserId(command.userId)
        purchasedItems.forEach { cart.removeItem(it.id) }
        cartPort.save(cart)

        return PurchaseResultDto(
            purchasedItems = purchasedItems.map { it.toDto(user) },
            remainingPoints = user.points.value
        )
    }

    @PreAuthorize("#command.userId == authentication.principal.id")
    override fun purchaseCartItems(command: PurchaseCartItemsCommand): PurchaseResultDto {
        val user = userPort.findByIdWithCollections(command.userId)
            ?: throw SoomSoomException(UserErrorCode.NOT_FOUND)

        val cart = cartPort.findByUserId(command.userId)
        // 장바구니에 아무것도 없다면 빈 값 반환
        if (cart.items.isEmpty()) {
            return PurchaseResultDto(emptyList(), user.points.value)
        }

        // 장바구니에서 구매할 아이템 목록 조회
        val itemsToPurchase = itemPort.findAllByIdsForUpdate(cart.items.map { it.itemId })

        // 이미 구매한 아이템인지 검증
        validateAlreadyOwned(itemsToPurchase.map { it.id }, user)

        // 구매 처리 및 구매 완료 이벤트 발행
        val purchasedItems = purchaseItemsInternal(user, itemsToPurchase, command.expectedTotalPrice)

        // 장바구니 비우기
        cart.clear()
        cartPort.save(cart)

        return PurchaseResultDto(
            purchasedItems = purchasedItems.map { it.toDto(user) },
            remainingPoints = user.points.value
        )
    }

    private fun validate(
        itemIds: List<Long>,
        user: User,
    ): List<Item> {
        // 이미 구매한 아이템이 존재하는지 확인
        validateAlreadyOwned(itemIds, user)

        val itemsToPurchase = itemPort.findAllByIdsForUpdate(itemIds)

        // 구매할 아이템이 전부 존재하는지 확인
        if (itemsToPurchase.size != itemIds.toSet().size) {
            throw SoomSoomException(ItemErrorCode.NOT_FOUND)
        }
        return itemsToPurchase
    }

    private fun validateAlreadyOwned(
        itemIds: List<Long>,
        user: User,
    ) {
        if (itemIds.any { it in user.ownedItems }) {
            throw SoomSoomException(PurchaseErrorCode.ALREADY_PURCHASE)
        }
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
