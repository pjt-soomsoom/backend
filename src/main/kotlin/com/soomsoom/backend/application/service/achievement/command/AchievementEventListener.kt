package com.soomsoom.backend.application.service.achievement.command

import com.soomsoom.backend.application.port.`in`.achievement.usecase.command.DeleteUserAchievedUseCase
import com.soomsoom.backend.application.port.`in`.achievement.usecase.command.DeleteUserProgressUseCase
import com.soomsoom.backend.application.port.`in`.achievement.usecase.command.UpdateUserProgressUseCase
import com.soomsoom.backend.common.event.Event
import com.soomsoom.backend.common.event.payload.ActivityCompletedPayload
import com.soomsoom.backend.common.event.payload.DiaryCreatedPayload
import com.soomsoom.backend.common.event.payload.ScreenTimeAccumulatedPayload
import com.soomsoom.backend.common.event.payload.UserDeletedPayload
import com.soomsoom.backend.common.event.payload.UserPlayTimeAccumulatedPayload
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional
import org.springframework.transaction.event.TransactionPhase
import org.springframework.transaction.event.TransactionalEventListener

@Component
class AchievementEventListener(
    private val updateUserProgressUseCase: UpdateUserProgressUseCase,
    private val userAchievedUseCase: DeleteUserAchievedUseCase,
    private val userProgressUseCase: DeleteUserProgressUseCase,
) {
    @Async("threadPoolTaskExecutor")
    @TransactionalEventListener(
        classes = [Event::class],
        condition = "#event.eventType == T(com.soomsoom.backend.common.event.EventType).DIARY_CREATED",
        phase = TransactionPhase.AFTER_COMMIT
    )
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    fun handleDiaryCreatedEvent(event: Event<DiaryCreatedPayload>) {
        updateUserProgressUseCase.updateProgress(event)
    }

    /**
     * ActivityCompleted 이벤트를 직접 구독하는 리스너
     */
    @Async("threadPoolTaskExecutor")
    @TransactionalEventListener(
        classes = [Event::class],
        condition = "#event.eventType == T(com.soomsoom.backend.common.event.EventType).ACTIVITY_COMPLETED",
        phase = TransactionPhase.AFTER_COMMIT
    )
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    fun handleActivityCompletedEvent(event: Event<ActivityCompletedPayload>) {
        updateUserProgressUseCase.updateProgress(event)
    }

    @Async("threadPoolTaskExecutor")
    @TransactionalEventListener(
        classes = [Event::class],
        condition = "#event.eventType == T(com.soomsoom.backend.common.event.EventType).USER_PLAY_TIME_ACCUMULATED",
        phase = TransactionPhase.AFTER_COMMIT
    )
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    fun handleUserPlayTimeAccumulatedEvent(event: Event<UserPlayTimeAccumulatedPayload>) {
        updateUserProgressUseCase.updateProgress(event)
    }

    @Async("threadPoolTaskExecutor")
    @TransactionalEventListener(
        classes = [Event::class],
        condition = "#event.eventType == T(com.soomsoom.backend.common.event.EventType).SCREEN_TIME_ACCUMULATED",
        phase = TransactionPhase.AFTER_COMMIT
    )
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    fun handleScreenTimeAccumulatedEvent(event: Event<ScreenTimeAccumulatedPayload>) {
        updateUserProgressUseCase.updateProgress(event)
    }

    /**
     * 유저 삭제 시 업적 기록 삭제
     */
    @TransactionalEventListener(
        classes = [Event::class],
        condition = "#event.eventType == T(com.soomsoom.backend.common.event.EventType).USER_DELETED",
        phase = TransactionPhase.BEFORE_COMMIT
    )
    fun handleUserDeletedEvent(event: Event<UserDeletedPayload>) {
        val payload = event.payload
        userAchievedUseCase.deleteByUserId(payload.userId)
        userProgressUseCase.deleteByUserId(payload.userId)
    }
}
