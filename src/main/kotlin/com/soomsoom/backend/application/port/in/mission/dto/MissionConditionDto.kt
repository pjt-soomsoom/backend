package com.soomsoom.backend.application.port.`in`.mission.dto

import com.soomsoom.backend.domain.mission.model.entity.MissionCondition
import com.soomsoom.backend.domain.mission.model.enums.ConditionType

/**
 * 미션 달성 조건을 나타내는 DTO입니다.
 */
data class MissionConditionDto(
    val type: ConditionType,
    val targetValue: Int,
) {
    companion object {
        fun from(condition: MissionCondition): MissionConditionDto {
            return MissionConditionDto(
                type = condition.type,
                targetValue = condition.targetValue
            )
        }
    }
}
