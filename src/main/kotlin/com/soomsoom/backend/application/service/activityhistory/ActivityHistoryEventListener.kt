package com.soomsoom.backend.application.service.activityhistory

import com.soomsoom.backend.application.port.`in`.activityhistory.command.RecordActivityProgressCommand
import com.soomsoom.backend.application.port.`in`.activityhistory.usecase.command.DeleteActivityCompletionLogUseCase
import com.soomsoom.backend.application.port.`in`.activityhistory.usecase.command.DeleteActivityProgressUseCase
import com.soomsoom.backend.application.port.`in`.activityhistory.usecase.command.DeleteUserActivitySummaryUseCase
import com.soomsoom.backend.application.port.`in`.activityhistory.usecase.command.RecordActivityProgressUseCase
import com.soomsoom.backend.common.event.Event
import com.soomsoom.backend.common.event.payload.ActivityCompletedPayload
import com.soomsoom.backend.common.event.payload.UserDeletedPayload
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional
import org.springframework.transaction.event.TransactionPhase
import org.springframework.transaction.event.TransactionalEventListener

@Component
class ActivityHistoryEventListener(
    private val recordActivityProgressUseCase: RecordActivityProgressUseCase,
    private val deleteActivityProgressUseCase: DeleteActivityProgressUseCase,
    private val deleteActivityCompletionLogUseCase: DeleteActivityCompletionLogUseCase,
    private val deleteUserActivitySummaryUseCase: DeleteUserActivitySummaryUseCase,
) {
    private val logger = LoggerFactory.getLogger(javaClass)

    @Async("threadPoolTaskExecutor")
    @TransactionalEventListener(
        classes = [Event::class],
        condition = "#event.eventType == T(com.soomsoom.backend.common.event.EventType).ACTIVITY_COMPLETED",
        phase = TransactionPhase.AFTER_COMMIT
    )
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    fun handleActivityCompletedEvent(event: Event<ActivityCompletedPayload>) {
        logger.info("handleActivityCompletedEvent start")

        val payload = event.payload
        val command = RecordActivityProgressCommand(
            userId = payload.userId,
            activityId = payload.activityId,
            lastPlaybackPosition = 0,
            actualPlayTimeInSeconds = 0
        )
        recordActivityProgressUseCase.record(command)
    }

    /**
     * 유저 삭제 시 activity 기록 삭제
     */
    @TransactionalEventListener(
        classes = [Event::class],
        condition = "#event.eventType == T(com.soomsoom.backend.common.event.EventType).USER_DELETED",
        phase = TransactionPhase.BEFORE_COMMIT
    )
    fun handleUserDeletedEvent(event: Event<UserDeletedPayload>) {
        val payload = event.payload
        deleteActivityProgressUseCase.deleteByUserId(payload.userId)
        deleteActivityCompletionLogUseCase.deleteByUserId(payload.userId)
        deleteUserActivitySummaryUseCase.deleteByUserId(payload.userId)
    }
}
