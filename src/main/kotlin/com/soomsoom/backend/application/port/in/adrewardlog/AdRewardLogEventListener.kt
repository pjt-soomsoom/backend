package com.soomsoom.backend.application.port.`in`.adrewardlog

import com.soomsoom.backend.application.port.`in`.adrewardlog.usecase.command.DeleteAdRewardLogUseCase
import com.soomsoom.backend.common.event.Event
import com.soomsoom.backend.common.event.payload.UserDeletedPayload
import org.springframework.stereotype.Component
import org.springframework.transaction.event.TransactionPhase
import org.springframework.transaction.event.TransactionalEventListener

@Component
class AdRewardLogEventListener(
    private val deleteAdRewardLogUseCase: DeleteAdRewardLogUseCase,
) {
    /**
     * 유저 삭제 시 보상 관고 시청 기록 삭제
     */
    @TransactionalEventListener(
        classes = [Event::class],
        condition = "#event.eventType == T(com.soomsoom.backend.common.event.EventType).USER_DELETED",
        phase = TransactionPhase.BEFORE_COMMIT
    )
    fun handleUserDeletedEvent(event: Event<UserDeletedPayload>) {
        val payload = event.payload
        deleteAdRewardLogUseCase.deleteByUserId(payload.userId)
    }
}
