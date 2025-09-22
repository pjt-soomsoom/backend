package com.soomsoom.backend.application.service.activityhistory.command

import com.soomsoom.backend.application.port.`in`.activityhistory.command.RecordActivityProgressCommand
import com.soomsoom.backend.application.port.`in`.activityhistory.usecase.command.RecordActivityProgressUseCase
import com.soomsoom.backend.application.port.out.activity.ActivityPort
import com.soomsoom.backend.application.port.out.activityhistory.ActivityHistoryPort
import com.soomsoom.backend.common.event.Event
import com.soomsoom.backend.common.event.EventType
import com.soomsoom.backend.common.event.payload.UserPlayTimeAccumulatedPayload
import com.soomsoom.backend.common.exception.SoomSoomException
import com.soomsoom.backend.domain.activity.ActivityErrorCode
import com.soomsoom.backend.domain.activity.model.enums.ActivityType
import com.soomsoom.backend.domain.activityhistory.model.ActivityProgress
import com.soomsoom.backend.domain.activityhistory.model.UserActivitySummary
import org.slf4j.LoggerFactory
import org.springframework.context.ApplicationEventPublisher
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class RecordActivityProgressService(
    private val activityHistoryPort: ActivityHistoryPort,
    private val eventPublisher: ApplicationEventPublisher,
    private val activityPort: ActivityPort,
) : RecordActivityProgressUseCase {

    private val logger = LoggerFactory.getLogger(javaClass)

    /**
     * 사용자의 활동 진행 상황을 기록
     * @param command 사용자 ID, 활동 ID, 마지막 재생 위치, 실제 재생 시간 정보
     */
    @PreAuthorize("hasRole('ADMIN') or #command.userId == authentication.principal.id")
    override fun record(command: RecordActivityProgressCommand) {
        logger.info("record start")
        logger.info("lastTime = {}", command.lastPlaybackPosition)
        // 이어듣기를 위한 진행 상황(ActivityProgress)을 저장하거나 갱신
        updateActivityProgress(command)

        if (command.lastPlaybackPosition == 0 && command.actualPlayTimeInSeconds == 0) {
            logger.info("end")
            return
        }

        val activity = activityPort.findById(command.activityId)
            ?: throw SoomSoomException(ActivityErrorCode.NOT_FOUND)

        // 누적 시간 통계(UserActivitySummary)를 갱신 -> MEDITATION, BREATHING 만 적용
        if (activity.type != ActivityType.SOUND_EFFECT) {
            updateUserActivitySummary(command.userId, command.actualPlayTimeInSeconds)
        }

        // 누적 시간이 갱신되었음을 시스템에 알리는 이벤트를 발행 (업적 시스템이 이 이벤트를 수신)
        publishUserPlayTimeAccumulatedEvent(activity.type, command.userId, command.actualPlayTimeInSeconds)
    }

    /**
     * ActivityProgress를 조회하여 없으면 생성하고, 있으면 받은 정보로 갱신
     */
    private fun updateActivityProgress(command: RecordActivityProgressCommand) {
        val progress = activityHistoryPort.findProgress(command.userId, command.activityId)
            ?: ActivityProgress(null, command.userId, command.activityId, 0, null, null)

        progress.progressSeconds = command.lastPlaybackPosition
        activityHistoryPort.saveProgress(progress)
    }

    /**
     * UserActivitySummary를 조회하여 없으면 생성하고, 있으면 실제 재생 시간을 더해 갱신
     */
    private fun updateUserActivitySummary(userId: Long, actualPlayTimeInSeconds: Int): UserActivitySummary {
        val summary = activityHistoryPort.findUserSummary(userId)
            ?: UserActivitySummary(null, userId, 0, null, null)

        summary.addPlayTime(actualPlayTimeInSeconds)
        return activityHistoryPort.saveUserSummary(summary)
    }

    /**
     * 누적 시간 갱신 이벤트를 발행
     */
    private fun publishUserPlayTimeAccumulatedEvent(activityType: ActivityType, userId: Long, actualPlayTimeInSeconds: Int) {
        val event = Event(
            eventType = EventType.USER_PLAY_TIME_ACCUMULATED,
            payload = UserPlayTimeAccumulatedPayload(
                userId = userId,
                actualPlayTimeInSeconds = actualPlayTimeInSeconds,
                activityType = activityType
            )
        )
        eventPublisher.publishEvent(event)
    }
}
