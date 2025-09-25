package com.soomsoom.backend.application.service.mission.strategy

import com.soomsoom.backend.application.port.`in`.mission.usecase.command.ClaimMissionRewardUseCase
import com.soomsoom.backend.application.port.out.mission.MissionCompletionLogPort
import com.soomsoom.backend.common.event.Payload
import com.soomsoom.backend.common.event.payload.ActivityCompletedPayload
import com.soomsoom.backend.common.utils.DateHelper
import com.soomsoom.backend.domain.activity.model.enums.ActivityType
import com.soomsoom.backend.domain.mission.model.aggregate.Mission
import com.soomsoom.backend.domain.mission.model.entity.MissionCompletionLog
import com.soomsoom.backend.domain.mission.model.enums.MissionType
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

/**
 * '일일 호흡 횟수' 타입의 미션(예: 하루 첫 호흡)을 처리
 */
@Component
@Transactional
class DailyBreathingCountStrategy(
    private val missionCompletionLogPort: MissionCompletionLogPort,
    private val claimMissionRewardUseCase: ClaimMissionRewardUseCase,
    private val dateHelper: DateHelper,
) : MissionProcessingStrategy {
    override fun getMissionType(): MissionType = MissionType.DAILY_BREATHING_COUNT

    override fun process(mission: Mission, payload: Payload) {
        if (payload !is ActivityCompletedPayload || payload.activityType != ActivityType.BREATHING) return

        handleProgress(mission, payload) {
        }
    }

    private fun handleProgress(mission: Mission, payload: ActivityCompletedPayload, updateLogic: () -> Unit) {
        val userId = payload.userId
        val now = payload.completedAt

        // 이미 완료된 미션인지 먼저 확인
        // '오늘' 날짜 기준으로 완료 기록이 있는지 확인
        val businessDay = dateHelper.getBusinessDay(now)
        val alreadyCompleted = missionCompletionLogPort.existsByCompletedAtBetween(userId, mission.id, businessDay.start, businessDay.end)
        if (alreadyCompleted) return

        updateLogic()

        // 목표 달성 여부를 확인
        if (mission.targetValue == 1) {
            // 완료 로그를 기록
            val log = MissionCompletionLog(userId = userId, missionId = mission.id, completedAt = now)
            missionCompletionLogPort.save(log)
        }
    }
}
