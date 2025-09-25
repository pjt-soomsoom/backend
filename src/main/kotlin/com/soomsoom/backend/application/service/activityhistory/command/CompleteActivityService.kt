package com.soomsoom.backend.application.service.activityhistory.command

import com.soomsoom.backend.application.port.`in`.activityhistory.command.CompleteActivityCommand
import com.soomsoom.backend.application.port.`in`.activityhistory.dto.ActivityCompleteResult
import com.soomsoom.backend.application.port.`in`.activityhistory.usecase.command.CompleteActivityUseCase
import com.soomsoom.backend.application.port.out.activity.ActivityPort
import com.soomsoom.backend.application.port.out.activityhistory.ActivityHistoryPort
import com.soomsoom.backend.application.port.out.mission.MissionCompletionLogPort
import com.soomsoom.backend.application.port.out.mission.MissionPort
import com.soomsoom.backend.common.event.Event
import com.soomsoom.backend.common.event.EventType
import com.soomsoom.backend.common.event.payload.ActivityCompletedPayload
import com.soomsoom.backend.common.exception.SoomSoomException
import com.soomsoom.backend.common.utils.DateHelper
import com.soomsoom.backend.domain.activity.ActivityErrorCode
import com.soomsoom.backend.domain.activity.model.enums.ActivityType
import com.soomsoom.backend.domain.activityhistory.model.ActivityCompletionLog
import com.soomsoom.backend.domain.mission.model.enums.MissionType
import org.springframework.context.ApplicationEventPublisher
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Service
@Transactional
class CompleteActivityService(
    private val activityHistoryPort: ActivityHistoryPort,
    private val activityPort: ActivityPort,
    private val dateHelper: DateHelper,
    private val eventPublisher: ApplicationEventPublisher,
    private val missionPort: MissionPort,
    private val missionCompletionLogPort: MissionCompletionLogPort,
) : CompleteActivityUseCase {

    /**
     * 사용자가 활동을 완료했음을 기록
     * @param command 사용자 ID, 활동 ID 정보
     */
    @PreAuthorize("hasRole('ADMIN') or #command.userId == authentication.principal.id")
    override fun complete(command: CompleteActivityCommand): ActivityCompleteResult {
        // 해당 activity가 실제로 존재하는지 확인
        val activity = activityPort.findById(command.activityId) ?: throw SoomSoomException(ActivityErrorCode.NOT_FOUND)

        val now = LocalDateTime.now()
        val businessDay = dateHelper.getBusinessDay(now)
        var rewardableMissionInfo: ActivityCompleteResult.RewardableMissionInfo? = null

        // 완료한 활동이 '호흡'일 경우에만 미션 확인 로직을 수행
        if (activity.type == ActivityType.BREATHING) {
            // '일일 호흡' 타입의 미션 조회
            val mission = missionPort.findByType(MissionType.DAILY_BREATHING_COUNT)
            if (mission != null) {
                // '오늘'의 비즈니스 시간 범위를 기준으로, 이 미션을 이미 완료했는지 확인
                val businessDay = dateHelper.getBusinessDay(now)
                val isCompletedToday = missionCompletionLogPort.existsByCompletedAtBetween(
                    command.userId,
                    mission.id,
                    businessDay.start,
                    businessDay.end
                )

                val findCompletedButUnrewardedLog = missionCompletionLogPort.findCompletedButUnrewardedLog(
                    command.userId,
                    mission.id,
                    businessDay.start,
                    businessDay.end
                )
                // 오늘 아직 완료하지 않았다면, 보상 가능 상태로 설정하고 '완료' 로그를 동기적으로 기록
                if (!isCompletedToday || findCompletedButUnrewardedLog != null) {
                    rewardableMissionInfo = ActivityCompleteResult.RewardableMissionInfo(
                        missionId = mission.id,
                        title = mission.title
                    )
                }
            }
        }

        // 완료 기록(ActivityCompletionLog)을 DB에 새로 생성하여 저장
        val log = ActivityCompletionLog(null, command.userId, command.activityId, activity.type, null)

        activityHistoryPort.saveCompletionLog(log)

        // 활동이 완료되었음을 시스템에 알리는 이벤트를 발행 (업적 시스템 및 알림 시스템이 이 이벤트를 수신)
        val event = Event(
            eventType = EventType.ACTIVITY_COMPLETED,
            payload = ActivityCompletedPayload(
                userId = command.userId,
                activityId = command.activityId,
                activityType = activity.type,
                completedAt = LocalDateTime.now()
            )
        )
        eventPublisher.publishEvent(event)

        return ActivityCompleteResult(
            activityId = activity.id!!,
            completionEffectTexts = activity.completionEffectTexts,
            rewardableMission = rewardableMissionInfo
        )
    }
}
