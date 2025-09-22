package com.soomsoom.backend.application.service.activityhistory

import com.soomsoom.backend.application.port.`in`.activityhistory.command.RecordActivityProgressCommand
import com.soomsoom.backend.application.port.`in`.activityhistory.usecase.command.RecordActivityProgressUseCase
import com.soomsoom.backend.common.event.Event
import com.soomsoom.backend.common.event.payload.ActivityCompletedPayload
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
}
