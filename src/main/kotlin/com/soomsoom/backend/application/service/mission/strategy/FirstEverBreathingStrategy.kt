package com.soomsoom.backend.application.service.mission.strategy

import com.soomsoom.backend.application.port.`in`.mission.command.ClaimMissionRewardCommand
import com.soomsoom.backend.application.port.`in`.mission.usecase.command.ClaimMissionRewardUseCase
import com.soomsoom.backend.application.port.out.mission.MissionCompletionLogPort
import com.soomsoom.backend.common.event.Payload
import com.soomsoom.backend.common.event.payload.ActivityCompletedPayload
import com.soomsoom.backend.domain.activity.model.enums.ActivityType
import com.soomsoom.backend.domain.mission.model.aggregate.Mission
import com.soomsoom.backend.domain.mission.model.entity.MissionCompletionLog
import com.soomsoom.backend.domain.mission.model.enums.MissionType
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

/**
 * 사용자의 생애 '첫 호흡' 관련 미션을 처리
 */
@Component
@Transactional
class FirstEverBreathingStrategy(
    private val missionCompletionLogPort: MissionCompletionLogPort,
    private val claimMissionRewardUseCase: ClaimMissionRewardUseCase,
) : MissionProcessingStrategy {

    override fun getMissionType(): MissionType = MissionType.FIRST_EVER_BREATHING

    override fun process(mission: Mission, payload: Payload) {
        if (payload !is ActivityCompletedPayload || payload.activityType != ActivityType.BREATHING) return

        handleProgress(mission, payload) {}
    }

    private fun handleProgress(mission: Mission, payload: ActivityCompletedPayload, updateLogic: () -> Unit) {
        val userId = payload.userId
        val now = payload.completedAt

        // 전체 기간에 대해 이미 완료했는지 확인
        val alreadyCompleted = missionCompletionLogPort.exists(userId, mission.id)
        if (alreadyCompleted) return

        updateLogic()

        // 목표 달성 여부를 확인
        if (mission.targetValue == 1) {
            // 완료 로그를 기록
            val log = MissionCompletionLog(userId = userId, missionId = mission.id, completedAt = now)
            missionCompletionLogPort.save(log)
            claimMissionRewardUseCase.claimReward(ClaimMissionRewardCommand(userId, mission.id))
        }
    }
}
