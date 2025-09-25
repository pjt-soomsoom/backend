package com.soomsoom.backend.application.service.mission

import com.soomsoom.backend.application.port.`in`.mission.command.ProcessMissionProgressCommand
import com.soomsoom.backend.application.port.`in`.mission.usecase.command.ProcessMissionProgressUseCase
import com.soomsoom.backend.common.event.Event
import com.soomsoom.backend.common.event.payload.ActivityCompletedPayload
import com.soomsoom.backend.common.event.payload.PageVisitedPayload
import com.soomsoom.backend.common.event.payload.UserAuthenticatedPayload
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional
import org.springframework.transaction.event.TransactionPhase
import org.springframework.transaction.event.TransactionalEventListener

@Component
class MissionEventListener(
    private val processMissionProgressUseCase: ProcessMissionProgressUseCase,
) {
    /**
     * 사용자가 인증(로그인/앱 접속)할 때 발생하는 이벤트를 처리합니다.
     * '연속 출석' 미션의 트리거가 됩니다.
     */
    @Async("threadPoolTaskExecutor")
    @TransactionalEventListener(
        classes = [Event::class],
        condition = "#event.eventType == T(com.soomsoom.backend.common.event.EventType).USER_AUTHENTICATED",
        phase = TransactionPhase.AFTER_COMMIT
    )
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    fun handleUserAuthenticatedEvent(event: Event<UserAuthenticatedPayload>) {
        processMissionProgressUseCase.process(ProcessMissionProgressCommand(event.payload))
    }

    /**
     * 사용자가 호흡 등 활동을 완료했을 때 발생하는 이벤트를 처리합니다.
     * '하루 첫 호흡', '생애 첫 호흡' 미션의 트리거가 됩니다.
     */
    @Async("threadPoolTaskExecutor")
    @TransactionalEventListener(
        classes = [Event::class],
        condition = "#event.eventType == T(com.soomsoom.backend.common.event.EventType).ACTIVITY_COMPLETED",
        phase = TransactionPhase.AFTER_COMMIT
    )
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    fun handleActivityCompletedEvent(event: Event<ActivityCompletedPayload>) {
        processMissionProgressUseCase.process(ProcessMissionProgressCommand(event.payload))
    }

    /**
     * 사용자가 특정 페이지를 방문했을 때 발생하는 이벤트를 처리합니다.
     * '야웅이 페이지 첫 방문' 미션의 트리거가 됩니다.
     */
    @Async("threadPoolTaskExecutor")
    @TransactionalEventListener(
        classes = [Event::class],
        condition = "#event.eventType == T(com.soomsoom.backend.common.event.EventType).PAGE_VISITED",
        phase = TransactionPhase.AFTER_COMMIT
    )
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    fun handlePageVisitedEvent(event: Event<PageVisitedPayload>) {
        processMissionProgressUseCase.process(ProcessMissionProgressCommand(event.payload))
    }
}
