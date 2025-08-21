package com.soomsoom.backend.application.service.activityhistory.command

import com.soomsoom.backend.application.port.`in`.activityhistory.command.CompleteActivityCommand
import com.soomsoom.backend.application.port.`in`.activityhistory.usecase.command.CompleteActivityUseCase
import com.soomsoom.backend.application.port.out.activity.ActivityPort
import com.soomsoom.backend.application.port.out.activityhistory.ActivityHistoryPort
import com.soomsoom.backend.common.event.Event
import com.soomsoom.backend.common.event.EventType
import com.soomsoom.backend.common.event.payload.ActivityCompletedNotificationPayload
import com.soomsoom.backend.common.exception.SoomSoomException
import com.soomsoom.backend.domain.activity.ActivityErrorCode
import com.soomsoom.backend.domain.activityhistory.model.ActivityCompletionLog
import org.springframework.context.ApplicationEventPublisher
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class CompleteActivityService(
    private val activityHistoryPort: ActivityHistoryPort,
    private val activityPort: ActivityPort,
    private val eventPublisher: ApplicationEventPublisher,
) : CompleteActivityUseCase {

    /**
     * 사용자가 활동을 완료했음을 기록
     * @param command 사용자 ID, 활동 ID 정보
     */
    @PreAuthorize("hasRole('ADMIN') or #command.userId == authentication.principal.id")
    override fun complete(command: CompleteActivityCommand) {
        // 해당 activity가 실제로 존재하는지 확인
        activityPort.findById(command.activityId) ?: throw SoomSoomException(ActivityErrorCode.NOT_FOUND)

        // 완료 기록(ActivityCompletionLog)을 DB에 새로 생성하여 저장
        val log = ActivityCompletionLog(null, command.userId, command.activityId, null)
        activityHistoryPort.saveCompletionLog(log)

        // 활동이 완료되었음을 시스템에 알리는 이벤트를 발행 (업적 시스템 및 알림 시스템이 이 이벤트를 수신)
        val event = Event(
            eventType = EventType.ACTIVITY_COMPLETED,
            payload = ActivityCompletedNotificationPayload(
                userId = command.userId,
                activityId = command.activityId
            )
        )
        eventPublisher.publishEvent(event)
    }
}
