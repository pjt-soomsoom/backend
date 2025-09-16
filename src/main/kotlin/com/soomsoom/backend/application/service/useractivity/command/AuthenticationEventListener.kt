package com.soomsoom.backend.application.service.useractivity.command

import com.soomsoom.backend.application.port.`in`.useractivity.command.RecordConnectionCommand
import com.soomsoom.backend.application.port.`in`.useractivity.usecase.command.RecordConnectionUseCase
import com.soomsoom.backend.common.event.Event
import com.soomsoom.backend.common.event.payload.UserAuthenticatedPayload
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional
import org.springframework.transaction.event.TransactionPhase
import org.springframework.transaction.event.TransactionalEventListener

@Component
class AuthenticationEventListener(
    private val recordConnectionUseCase: RecordConnectionUseCase,
) {
    @Async("threadPoolTaskExecutor")
    @TransactionalEventListener(
        classes = [Event::class],
        condition = "#event.eventType == T(com.soomsoom.backend.common.event.EventType).USER_AUTHENTICATED",
        phase = TransactionPhase.AFTER_COMMIT
    )
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    fun handleUserAuthenticatedEvent(event: Event<UserAuthenticatedPayload>) {
        val payload = event.payload
        val command = RecordConnectionCommand(userId = payload.userId)
        recordConnectionUseCase.recordConnection(command)
    }
}
