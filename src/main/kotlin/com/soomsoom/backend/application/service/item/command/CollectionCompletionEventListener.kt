package com.soomsoom.backend.application.service.item.command

import com.soomsoom.backend.application.port.out.user.UserOwnedCollectionPort
import com.soomsoom.backend.application.port.out.user.UserPort
import com.soomsoom.backend.common.event.Event
import com.soomsoom.backend.common.event.payload.ItemPurchasedPayload
import com.soomsoom.backend.common.exception.SoomSoomException
import com.soomsoom.backend.domain.user.UserErrorCode
import org.springframework.stereotype.Component
import org.springframework.transaction.event.TransactionPhase
import org.springframework.transaction.event.TransactionalEventListener

@Component
class CollectionCompletionEventListener(
    private val userPort: UserPort,
    private val userOwnedCollectionPort: UserOwnedCollectionPort,
) {
    @TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT)
    fun handleItemPurchasedEvent(event: Event<ItemPurchasedPayload>) {
        val payload = event.payload
        val userId = payload.userId
        val purchasedItemId = payload.itemId

        val completedCollectionIds = userOwnedCollectionPort.findCompletedCollectionIds(userId, purchasedItemId)

        if (completedCollectionIds.isNotEmpty()) {
            val user = userPort.findById(userId)
                ?: throw SoomSoomException(UserErrorCode.NOT_FOUND)

            completedCollectionIds.forEach { collectionId ->
                user.ownCollection(collectionId)
            }

            userPort.save(user)
        }
    }
}
