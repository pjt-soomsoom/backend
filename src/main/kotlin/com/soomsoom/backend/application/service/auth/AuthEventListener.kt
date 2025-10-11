package com.soomsoom.backend.application.service.auth

import com.soomsoom.backend.application.port.`in`.auth.usecase.command.RefreshTokenUseCase
import com.soomsoom.backend.common.event.Event
import com.soomsoom.backend.common.event.payload.UserDeletedPayload
import org.springframework.stereotype.Component
import org.springframework.transaction.event.TransactionPhase
import org.springframework.transaction.event.TransactionalEventListener

@Component
class AuthEventListener(
    private val refreshTokenUseCase: RefreshTokenUseCase,
) {
    /**
     * 유저 삭제 시 refresh token 삭제
     */
    @TransactionalEventListener(
        classes = [Event::class],
        condition = "#event.eventType == T(com.soomsoom.backend.common.event.EventType).USER_DELETED",
        phase = TransactionPhase.BEFORE_COMMIT
    )
    fun handleUserDeletedEvent(event: Event<UserDeletedPayload>) {
        val payload = event.payload
        refreshTokenUseCase.deleteByUserId(payload.userId)
    }
}
