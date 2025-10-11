package com.soomsoom.backend.application.service.purchase

import com.soomsoom.backend.application.port.`in`.purchase.usecase.DeletePurchaseLogUseCase
import com.soomsoom.backend.common.event.Event
import com.soomsoom.backend.common.event.payload.UserDeletedPayload
import org.springframework.stereotype.Component
import org.springframework.transaction.event.TransactionPhase
import org.springframework.transaction.event.TransactionalEventListener

@Component
class PurchaseEventListener(
    private val deletePurchaseLogUseCase: DeletePurchaseLogUseCase,
) {

    @TransactionalEventListener(
        classes = [Event::class],
        condition = "#event.eventType == T(com.soomsoom.backend.common.event.EventType).USER_DELETED",
        phase = TransactionPhase.BEFORE_COMMIT
    )
    fun handleUserDeletedEvent(event: Event<UserDeletedPayload>) {
        val payload = event.payload
        deletePurchaseLogUseCase.deleteByUserId(payload.userId)
    }
}
