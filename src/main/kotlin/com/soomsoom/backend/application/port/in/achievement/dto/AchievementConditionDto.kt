package com.soomsoom.backend.application.port.`in`.achievement.dto

import com.soomsoom.backend.domain.achievement.model.AchievementCondition
import com.soomsoom.backend.domain.achievement.model.ConditionType

data class AchievementConditionDto(
    val id: Long,
    val type: ConditionType,
    val targetValue: Int,
) {
    companion object {
        fun from(condition: AchievementCondition): AchievementConditionDto {
            return AchievementConditionDto(
                id = condition.id,
                type = condition.type,
                targetValue = condition.targetValue
            )
        }
    }
}
