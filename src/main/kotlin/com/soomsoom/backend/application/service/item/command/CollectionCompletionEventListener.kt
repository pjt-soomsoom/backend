package com.soomsoom.backend.application.service.item.command

import com.soomsoom.backend.application.port.out.item.CollectionPort
import com.soomsoom.backend.application.port.out.user.UserOwnedCollectionPort
import com.soomsoom.backend.application.port.out.user.UserPort
import com.soomsoom.backend.common.event.Event
import com.soomsoom.backend.common.event.payload.ItemOwnedPayload
import com.soomsoom.backend.common.event.payload.ItemsEquippedPayload
import com.soomsoom.backend.common.exception.SoomSoomException
import com.soomsoom.backend.domain.user.UserErrorCode
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional
import org.springframework.transaction.event.TransactionPhase
import org.springframework.transaction.event.TransactionalEventListener

@Component
class CollectionCompletionEventListener(
    private val userPort: UserPort,
    private val userOwnedCollectionPort: UserOwnedCollectionPort,
    private val collectionPort: CollectionPort,
) {
    @TransactionalEventListener(
        classes = [Event::class],
        condition = "#event.eventType == T(com.soomsoom.backend.common.event.EventType).ITEM_OWNED",
        phase = TransactionPhase.BEFORE_COMMIT
    )
    fun handleItemPurchasedEvent(event: Event<ItemOwnedPayload>) {
        val payload = event.payload
        val userId = payload.userId
        val ownedItemId = payload.itemId

        val completedCollectionIds = userOwnedCollectionPort.findCompletedCollectionIds(userId, ownedItemId)

        if (completedCollectionIds.isNotEmpty()) {
            val user = userPort.findByIdWithCollections(userId)
                ?: throw SoomSoomException(UserErrorCode.NOT_FOUND)

            completedCollectionIds.forEach { collectionId ->
                user.ownCollection(collectionId)
            }

            userPort.save(user)
        }
    }

    @Async("threadPoolTaskExecutor")
    @TransactionalEventListener(
        classes = [Event::class],
        condition = "#event.eventType == T(com.soomsoom.backend.common.event.EventType).ITEMS_EQUIPPED",
        phase = TransactionPhase.AFTER_COMMIT
    )
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    fun handleItemEquippedEvent(event: Event<ItemsEquippedPayload>) {
        val payload = event.payload
        val completableCollections = collectionPort.findCompletableCollections(payload.equippedItemIds)
        val completedCollectionIds = completableCollections.map { it.id }.toSet()

        val user = userPort.findByIdWithCollections(payload.userId)
            ?: throw SoomSoomException(UserErrorCode.NOT_FOUND)
        user.updateEquippedCollections(completedCollectionIds)

        userPort.save(user)
    }
}
