package com.soomsoom.backend.application.service.useractivity

import com.soomsoom.backend.application.port.`in`.useractivity.usecase.command.DeleteConnectionLogUseCase
import com.soomsoom.backend.application.port.`in`.useractivity.usecase.command.DeleteScreenTimeLogUseCase
import com.soomsoom.backend.common.event.Event
import com.soomsoom.backend.common.event.payload.UserDeletedPayload
import org.springframework.stereotype.Component
import org.springframework.transaction.event.TransactionPhase
import org.springframework.transaction.event.TransactionalEventListener

@Component
class UserActivityEventListener(
    private val deleteConnectionLogUseCase: DeleteConnectionLogUseCase,
    private val deleteScreenTimeLogUseCase: DeleteScreenTimeLogUseCase,
) {
    @TransactionalEventListener(
        classes = [Event::class],
        condition = "#event.eventType == T(com.soomsoom.backend.common.event.EventType).USER_DELETED",
        phase = TransactionPhase.BEFORE_COMMIT
    )
    fun handleUserDeletedEvent(event: Event<UserDeletedPayload>) {
        val payload = event.payload
        deleteConnectionLogUseCase.deleteByUserId(userId = payload.userId)
        deleteScreenTimeLogUseCase.deleteByUserId(userId = payload.userId)
    }
}
