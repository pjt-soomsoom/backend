package com.soomsoom.backend.application.service.mission.strategy

import com.soomsoom.backend.application.port.out.mission.MissionCompletionLogPort
import com.soomsoom.backend.application.port.out.mission.UserMissionProgressPort
import com.soomsoom.backend.common.event.Event
import com.soomsoom.backend.common.event.EventType
import com.soomsoom.backend.common.event.Payload
import com.soomsoom.backend.common.event.payload.DiaryCreatedPayload
import com.soomsoom.backend.common.event.payload.MissionCompletedNotificationPayload
import com.soomsoom.backend.common.utils.DateHelper
import com.soomsoom.backend.domain.mission.model.aggregate.Mission
import com.soomsoom.backend.domain.mission.model.entity.MissionCompletionLog
import com.soomsoom.backend.domain.mission.model.entity.UserMissionProgress
import com.soomsoom.backend.domain.mission.model.enums.ClaimType
import com.soomsoom.backend.domain.mission.model.enums.MissionType
import com.soomsoom.backend.domain.mission.model.enums.RepeatableType
import com.soomsoom.backend.domain.mission.model.vo.DailyCountProgress
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
@Transactional
class DiaryCountStrategy(
    private val userMissionProgressPort: UserMissionProgressPort,
    private val missionCompletionLogPort: MissionCompletionLogPort,
    private val eventPublisher: ApplicationEventPublisher,
    private val dateHelper: DateHelper,
) : MissionProcessingStrategy {
    override fun getMissionType() = MissionType.DIARY_COUNT

    override fun process(mission: Mission, payload: Payload) {
        if (payload !is DiaryCreatedPayload) return

        handleProgress(mission, payload) { progress ->
            val now = payload.createdAt
            // DailyCountProgress를 사용하여 마지막 작성 시간을 기록
            val progressData = progress.progressData as? DailyCountProgress ?: DailyCountProgress(0, null)

            val currentBusinessDate = dateHelper.getBusinessDate(dateHelper.toZonedDateTimeInUtc(now))
            val lastBusinessDate = progressData.lastTimestamp?.let { dateHelper.getBusinessDate(dateHelper.toZonedDateTimeInUtc(it)) }

            // 마지막 작성일과 오늘 날짜가 다를 경우에만 카운트 증가
            if (currentBusinessDate != lastBusinessDate) {
                progress.progressData = DailyCountProgress(progressData.count + 1, now)
            }
        }
    }

    private fun handleProgress(mission: Mission, payload: DiaryCreatedPayload, updateLogic: (UserMissionProgress) -> Unit) {
        val userId = payload.userId
        val now = payload.createdAt

        // 일회성 미션(NONE) 완료 여부 확인
        if (mission.repeatableType == RepeatableType.NONE) {
            if (missionCompletionLogPort.exists(userId, mission.id)) return
        }

        val progress = userMissionProgressPort.findOrCreate(userId, mission.id)
        val countBeforeUpdate = (progress.progressData as? DailyCountProgress)?.count ?: 0

        updateLogic(progress)

        val progressDataAfterUpdate = progress.progressData as? DailyCountProgress
        val countAfterUpdate = progressDataAfterUpdate?.count ?: 0

        // 카운트가 실제로 증가했을 때만 저장 및 완료 확인 로직 수행
        if (countAfterUpdate > countBeforeUpdate) {
            userMissionProgressPort.save(progress)

            if (countAfterUpdate >= mission.targetValue) {
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
}
