package com.soomsoom.backend.domain.achievement.model

class AchievementCondition(
    val id: Long,
    val achievementId: Long,
    val type: ConditionType,
    val targetValue: Int,
)
