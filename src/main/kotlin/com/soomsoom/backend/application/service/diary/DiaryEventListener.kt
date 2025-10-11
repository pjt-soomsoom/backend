package com.soomsoom.backend.application.service.diary

import com.soomsoom.backend.application.port.`in`.diary.usecase.command.DeleteDiaryUseCase
import com.soomsoom.backend.common.event.Event
import com.soomsoom.backend.common.event.payload.UserDeletedPayload
import org.springframework.stereotype.Component
import org.springframework.transaction.event.TransactionPhase
import org.springframework.transaction.event.TransactionalEventListener

@Component
class DiaryEventListener(
    private val deleteDiaryUseCase: DeleteDiaryUseCase,
) {
    /**
     * 유저 삭제 시 감정 기록 삭제
     */
    @TransactionalEventListener(
        classes = [Event::class],
        condition = "#event.eventType == T(com.soomsoom.backend.common.event.EventType).USER_DELETED",
        phase = TransactionPhase.BEFORE_COMMIT
    )
    fun handleUserDeletedEvent(event: Event<UserDeletedPayload>) {
        val payload = event.payload
        deleteDiaryUseCase.deleteByUserId(payload.userId)
    }
}
