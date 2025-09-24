package com.soomsoom.backend.application.service.mission.strategy

import com.soomsoom.backend.application.port.`in`.mission.command.ClaimMissionRewardCommand
import com.soomsoom.backend.application.port.`in`.mission.usecase.command.ClaimMissionRewardUseCase
import com.soomsoom.backend.application.port.out.mission.MissionCompletionLogPort
import com.soomsoom.backend.application.port.out.mission.UserMissionProgressPort
import com.soomsoom.backend.common.event.Payload
import com.soomsoom.backend.common.event.payload.UserAuthenticatedPayload
import com.soomsoom.backend.common.utils.DateHelper
import com.soomsoom.backend.domain.mission.model.aggregate.Mission
import com.soomsoom.backend.domain.mission.model.entity.MissionCompletionLog
import com.soomsoom.backend.domain.mission.model.entity.UserMissionProgress
import com.soomsoom.backend.domain.mission.model.enums.ClaimType
import com.soomsoom.backend.domain.mission.model.enums.MissionType
import com.soomsoom.backend.domain.mission.model.enums.RepeatableType
import com.soomsoom.backend.domain.mission.model.vo.AttendanceStreakProgress
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

/**
 * '연속 출석' 관련 미션을 처리하는 전략 클래스
 */
@Component
@Transactional
class ConsecutiveAttendanceStrategy(
    private val userMissionProgressPort: UserMissionProgressPort,
    private val missionCompletionLogPort: MissionCompletionLogPort,
    private val claimMissionRewardUseCase: ClaimMissionRewardUseCase,
    private val dateHelper: DateHelper,
) : MissionProcessingStrategy {
    override fun getMissionType() = MissionType.CONSECUTIVE_ATTENDANCE

    /**
     * '출석' 이벤트가 발생했을 때, 관련된 모든 '연속 출석' 미션의 진행도를 업데이트합니다.
     */
    override fun process(mission: Mission, payload: Payload) {
        if (payload !is UserAuthenticatedPayload) return

        // handleProgress 헬퍼 메서드를 호출하여 진행도 업데이트 로직을 위임합니다.
        handleProgress(mission, payload) { progress ->
            val now = payload.authenticatedAt
            val progressData = progress.progressData as? AttendanceStreakProgress
                ?: AttendanceStreakProgress(0, null)

            val currentBusinessDate = dateHelper.getBusinessDate(now)

            val newStreak = if (progressData.lastAttendanceTimestamp == null) {
                1
            } else {
                val lastBusinessDate = dateHelper.getBusinessDate(progressData.lastAttendanceTimestamp)
                when {
                    currentBusinessDate == lastBusinessDate -> progressData.streak
                    currentBusinessDate == lastBusinessDate.plusDays(1) -> progressData.streak + 1
                    else -> 1
                }
            }

            // 변경된 진행도 데이터를 progress 객체에 다시 할당합니다.
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
    private fun handleProgress(mission: Mission, payload: UserAuthenticatedPayload, updateLogic: (UserMissionProgress) -> Unit) {
        val userId = payload.userId
        val now = payload.authenticatedAt

        // 1. [최적화] 이미 완료된 미션인지 먼저 확인합니다.
        val alreadyCompleted = when (mission.repeatableType) {
            RepeatableType.DAILY -> {
                val businessDay = dateHelper.getBusinessDay(now)
                missionCompletionLogPort.existsByCompletedAtBetween(userId, mission.id, businessDay.start, businessDay.end)
            }
            RepeatableType.NONE -> missionCompletionLogPort.existsBy(userId, mission.id)
            RepeatableType.WEEKLY -> return
        }
        if (alreadyCompleted) return

        // 2. 진행도를 가져오거나 새로 생성합니다.
        val progress = userMissionProgressPort.findOrCreate(userId, mission.id)

        // 3. 주입받은 updateLogic을 실행하여 진행도를 업데이트합니다.
        updateLogic(progress)
        userMissionProgressPort.save(progress)

        // 4. 업데이트된 진행도가 목표치에 도달했는지 확인합니다.
        val currentStreak = (progress.progressData as AttendanceStreakProgress).streak
        if (currentStreak >= mission.targetValue) {
            // 5. 완료 로그를 기록하고, 자동 보상일 경우 즉시 보상을 지급합니다.
            val log = MissionCompletionLog(userId = userId, missionId = mission.id, completedAt = now)
            missionCompletionLogPort.save(log)

            if (mission.claimType == ClaimType.AUTOMATIC) {
                claimMissionRewardUseCase.claimReward(ClaimMissionRewardCommand(userId, mission.id))
            }
        }
    }
}
