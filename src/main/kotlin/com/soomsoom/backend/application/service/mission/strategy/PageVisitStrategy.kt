package com.soomsoom.backend.application.service.mission.strategy

import com.soomsoom.backend.application.port.out.mission.MissionCompletionLogPort
import com.soomsoom.backend.common.event.Payload
import com.soomsoom.backend.common.event.payload.PageVisitedPayload
import com.soomsoom.backend.domain.mission.model.aggregate.Mission
import com.soomsoom.backend.domain.mission.model.entity.MissionCompletionLog
import com.soomsoom.backend.domain.mission.model.enums.MissionType
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

/**
 * '특정 페이지 첫 방문' 타입의 미션을 처리
 */
@Component
@Transactional
class PageVisitStrategy(
    private val missionCompletionLogPort: MissionCompletionLogPort,
    @Value("\${mission.page-visit.identifier.yawoongi}")
    private val yawoongiPageIdentifier: String,
) : MissionProcessingStrategy {
    override fun getMissionType(): MissionType = MissionType.FIRST_PAGE_VISIT

    override fun process(mission: Mission, payload: Payload) {
        if (payload !is PageVisitedPayload) return

        // 여기서는 미션의 title을 식별자로 사용한다고 가정합니다.
        if (payload.pageIdentifier == yawoongiPageIdentifier) {
            handleProgress(mission, payload)
        }
    }

    private fun handleProgress(mission: Mission, payload: PageVisitedPayload) {
        val userId = payload.userId
        val now = LocalDateTime.now()

        // 일회성 미션이므로, 전체 기간에 대해 이미 완료했는지 확인합니다.
        val alreadyCompleted = missionCompletionLogPort.existsBy(userId, mission.id)
        if (alreadyCompleted) return

        if (mission.targetValue == 1) {
            val log = MissionCompletionLog(userId = userId, missionId = mission.id, completedAt = now)
            missionCompletionLogPort.save(log)
        }
    }
}
