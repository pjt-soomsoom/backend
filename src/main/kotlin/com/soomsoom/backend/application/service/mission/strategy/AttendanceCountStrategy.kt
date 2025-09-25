package com.soomsoom.backend.application.service.mission.strategy

import com.soomsoom.backend.application.port.out.mission.MissionCompletionLogPort
import com.soomsoom.backend.application.port.out.mission.UserMissionProgressPort
import com.soomsoom.backend.common.event.Event
import com.soomsoom.backend.common.event.EventType
import com.soomsoom.backend.common.event.Payload
import com.soomsoom.backend.common.event.payload.FirstConnectionPayload
import com.soomsoom.backend.common.event.payload.MissionCompletedNotificationPayload
import com.soomsoom.backend.common.utils.DateHelper
import com.soomsoom.backend.domain.mission.model.aggregate.Mission
import com.soomsoom.backend.domain.mission.model.entity.MissionCompletionLog
import com.soomsoom.backend.domain.mission.model.entity.UserMissionProgress
import com.soomsoom.backend.domain.mission.model.enums.ClaimType
import com.soomsoom.backend.domain.mission.model.enums.MissionType
import com.soomsoom.backend.domain.mission.model.enums.RepeatableType
import com.soomsoom.backend.domain.mission.model.vo.SimpleCountProgress
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Transactional
@Component
class AttendanceCountStrategy(
    private val userMissionProgressPort: UserMissionProgressPort,
    private val missionCompletionLogPort: MissionCompletionLogPort,
    private val eventPublisher: ApplicationEventPublisher,
    private val dateHelper: DateHelper,
) : MissionProcessingStrategy {
    override fun getMissionType() = MissionType.ATTENDANCE_COUNT

    override fun process(mission: Mission, payload: Payload) {
        if (payload !is FirstConnectionPayload) return

        handleProgress(mission, payload) { progress ->
            val progressData = progress.progressData as? SimpleCountProgress ?: SimpleCountProgress(0)
            // 이벤트 자체가 '하루 첫 접속'을 보장하므로, 바로 카운트를 1 증가
            progress.progressData = SimpleCountProgress(progressData.count + 1)
        }
    }

    private fun handleProgress(mission: Mission, payload: FirstConnectionPayload, updateLogic: (UserMissionProgress) -> Unit) {
        val userId = payload.userId
        val now = LocalDateTime.now()

        // 누적 출석 미션은 반복 불가능
        if (mission.repeatableType == RepeatableType.NONE) {
            if (missionCompletionLogPort.exists(userId, mission.id)) return
        }

        val progress = userMissionProgressPort.findOrCreate(userId, mission.id)

        updateLogic(progress)
        userMissionProgressPort.save(progress)

        val currentCount = (progress.progressData as SimpleCountProgress).count
        if (currentCount >= mission.targetValue) {
            val log = MissionCompletionLog(userId = userId, missionId = mission.id, completedAt = now)
            missionCompletionLogPort.save(log)

            if (mission.claimType == ClaimType.AUTOMATIC) {
                eventPublisher.publishEvent(
                    Event(
                        eventType = EventType.MISSION_COMPLETED,
                        payload = MissionCompletedNotificationPayload(
                            userId = userId,
                            missionId = mission.id,
                            missionName = mission.title,
                            title = mission.completionNotification.title,
                            body = mission.completionNotification.body,
                            reward = mission.reward
                        )
                    )
                )
            }
        }
    }
}
