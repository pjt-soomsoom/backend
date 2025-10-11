package com.soomsoom.backend.application.service.user

import com.soomsoom.backend.application.port.`in`.user.command.GrantItemToUserCommand
import com.soomsoom.backend.application.port.`in`.user.command.GrantItemsToUserCommand
import com.soomsoom.backend.application.port.`in`.user.usecase.command.DeleteCartUseCase
import com.soomsoom.backend.application.port.`in`.user.usecase.command.GrantItemToUserUseCase
import com.soomsoom.backend.application.port.out.item.ItemPort
import com.soomsoom.backend.common.event.Event
import com.soomsoom.backend.common.event.payload.ItemCreatedPayload
import com.soomsoom.backend.common.event.payload.ItemPurchasedPayload
import com.soomsoom.backend.common.event.payload.UserCreatedPayload
import com.soomsoom.backend.common.event.payload.UserDeletedPayload
import com.soomsoom.backend.domain.item.model.enums.AcquisitionType
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional
import org.springframework.transaction.event.TransactionPhase
import org.springframework.transaction.event.TransactionalEventListener

@Component
class UserEventListener(
    private val grantItemToUserUseCase: GrantItemToUserUseCase,
    private val deleteCartUseCase: DeleteCartUseCase,
    private val itemPort: ItemPort,
) {

    @TransactionalEventListener(
        classes = [Event::class],
        condition = "#event.eventType == T(com.soomsoom.backend.common.event.EventType).ITEM_PURCHASED",
        phase = TransactionPhase.AFTER_COMMIT
    )
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    fun handleItemPurchasedEvent(event: Event<ItemPurchasedPayload>) {
        val payload = event.payload
        grantItemToUserUseCase.grantItemToUser(GrantItemToUserCommand(payload.userId, payload.itemId))
    }

    @TransactionalEventListener(
        classes = [Event::class],
        condition = "#event.eventType == T(com.soomsoom.backend.common.event.EventType).USER_CREATED",
        phase = TransactionPhase.AFTER_COMMIT
    )
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    fun handleUserCreatedEvent(event: Event<UserCreatedPayload>) {
        val payload = event.payload

        val itemIds = itemPort.findAllByAcquisitionType(acquisitionType = AcquisitionType.DEFAULT).map { it.id }

        val command = GrantItemsToUserCommand(
            userId = payload.userId,
            itemIds = itemIds
        )
        grantItemToUserUseCase.grantItemsToUser(command)
    }

    @TransactionalEventListener(
        classes = [Event::class],
        condition = "#event.eventType == T(com.soomsoom.backend.common.event.EventType).ITEM_CREATED",
        phase = TransactionPhase.AFTER_COMMIT
    )
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    fun handleItemCreatedEvent(event: Event<ItemCreatedPayload>) {
        val payload = event.payload
        if (payload.acquisitionType == AcquisitionType.DEFAULT) {
            grantItemToUserUseCase.grantItemToAllUsers(payload.itemId)
        }
    }

    @TransactionalEventListener(
        classes = [Event::class],
        condition = "#event.eventType == T(com.soomsoom.backend.common.event.EventType).USER_DELETED",
        phase = TransactionPhase.BEFORE_COMMIT
    )
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    fun handleUserDeletedEvent(event: Event<UserDeletedPayload>) {
        val payload = event.payload
        deleteCartUseCase.deleteByUserId(payload.userId)
    }
}
