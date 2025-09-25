package com.soomsoom.backend.application.service.mission.strategy

import com.soomsoom.backend.common.event.Payload
import com.soomsoom.backend.domain.mission.model.aggregate.Mission
import com.soomsoom.backend.domain.mission.model.enums.MissionType

interface MissionProcessingStrategy {
    /**
     * 이 전략이 어떤 MissionType을 처리하는지 명시.
     * Locator가 이 값을 보고 적절한 Strategy를 찾아줌
     */
    fun getMissionType(): MissionType

    /**
     * 실제 미션 진행도 업데이트 및 완료 처리를 수행
     * @param payload 미션 진행을 유발한 이벤트 페이로드
     */
    fun process(mission: Mission, payload: Payload)
}
