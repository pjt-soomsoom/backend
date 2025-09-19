package com.soomsoom.backend.application.port.`in`.achievement.dto

import com.soomsoom.backend.domain.achievement.model.entity.AchievementCondition
import com.soomsoom.backend.domain.achievement.model.enums.ConditionType
import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "업적 조건 정보 DTO")
data class AchievementConditionDto(
    @field:Schema(description = "업적 조건 타입") val type: ConditionType,
    @field:Schema(description = "업적 목표값") val targetValue: Int,
) {
    companion object {
        fun from(condition: AchievementCondition): AchievementConditionDto {
            return AchievementConditionDto(
                type = condition.type,
                targetValue = condition.targetValue
            )
        }
    }
}
