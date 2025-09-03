package com.soomsoom.backend.application.port.`in`.achievement.dto

import com.soomsoom.backend.domain.achievement.model.Achievement
import com.soomsoom.backend.domain.achievement.model.AchievementCategory
import com.soomsoom.backend.domain.achievement.model.AchievementCondition
import com.soomsoom.backend.domain.achievement.model.AchievementGrade

data class AchievementDto(
    val id: Long,
    val name: String,
    val description: String,
    val phrase: String?,
    val grade: AchievementGrade,
    val category: AchievementCategory,
    val rewardPoints: Int?,
    val rewardItemId: Long?,
    val conditions: List<AchievementConditionDto>,
) {
    companion object {
        fun from(achievement: Achievement, conditions: List<AchievementCondition>): AchievementDto {
            return AchievementDto(
                id = achievement.id,
                name = achievement.name,
                description = achievement.description,
                phrase = achievement.phrase,
                grade = achievement.grade,
                category = achievement.category,
                rewardPoints = achievement.rewardPoints,
                rewardItemId = achievement.rewardItemId,
                conditions = conditions.map { AchievementConditionDto.from(it) }
            )
        }
    }
}
