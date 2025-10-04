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
import com.soomsoom.backend.domain.mission.model.vo.AttendanceStreakProgress
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

/**
 * '연속 출석' 관련 미션을 처리하는 전략 클래스
 */
@Component
@Transactional
class ConsecutiveAttendanceStrategy(
    private val userMissionProgressPort: UserMissionProgressPort,
    private val missionCompletionLogPort: MissionCompletionLogPort,
    private val eventPublisher: ApplicationEventPublisher,
    private val dateHelper: DateHelper,
) : MissionProcessingStrategy {
    override fun getMissionType() = MissionType.CONSECUTIVE_ATTENDANCE

    /**
     * '출석' 이벤트가 발생했을 때, 관련된 모든 '연속 출석' 미션의 진행도를 업데이트합니다.
     */
    override fun process(mission: Mission, payload: Payload) {
        if (payload !is FirstConnectionPayload) return

        handleProgress(mission, payload) { progress ->
            val progressData = progress.progressData as? AttendanceStreakProgress
                ?: AttendanceStreakProgress(0, null)

            val currentBusinessDate = payload.businessDate
            val now = currentBusinessDate.atStartOfDay() // 이벤트 발생 날짜의 시작 시간

            val newStreak = if (progressData.lastAttendanceTimestamp == null) {
                1
            } else {
                val lastBusinessDate = dateHelper.getBusinessDate(dateHelper.toZonedDateTimeInUtc(progressData.lastAttendanceTimestamp))
                if (currentBusinessDate.isEqual(lastBusinessDate.plusDays(1))) {
                    progressData.streak + 1 // 연속된 날짜면 streak 증가
                } else {
                    1 // 연속이 깨졌으면 1로 리셋
                }
            }

            progress.progressData = AttendanceStreakProgress(newStreak, now)
        }
    }

    /**
     * 'achievement' 도메인의 패턴을 차용한 헬퍼 메서드입니다.
     * 미션 진행도 업데이트의 공통적인 절차(조회, 완료 확인, 업데이트, 저장, 보상)를 처리합니다.
     *
     * @param mission 현재 처리 중인 미션
     * @param payload 이벤트를 유발한 페이로드
     * @param updateLogic 실제 진행도(UserMissionProgress)를 어떻게 업데이트할지에 대한 로직
     */
    private fun handleProgress(mission: Mission, payload: FirstConnectionPayload, updateLogic: (UserMissionProgress) -> Unit) {
        val userId = payload.userId
        val now = LocalDateTime.now()

        // 이미 완료된 미션인지 먼저 확인
        if (mission.repeatableType == RepeatableType.NONE) {
            if (missionCompletionLogPort.exists(userId, mission.id)) return
        }

        val progress = userMissionProgressPort.findOrCreate(userId, mission.id)
        val streakBeforeUpdate = (progress.progressData as? AttendanceStreakProgress)?.streak ?: 0

        updateLogic(progress)

        val streakAfterUpdate = (progress.progressData as AttendanceStreakProgress).streak

        // 연속 기록이 변경되었을 때만 저장
        if (streakAfterUpdate != streakBeforeUpdate) {
            userMissionProgressPort.save(progress)
        }

        if (streakAfterUpdate >= mission.targetValue) {
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
