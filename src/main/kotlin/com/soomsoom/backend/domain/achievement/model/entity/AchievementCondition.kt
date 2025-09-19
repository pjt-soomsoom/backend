package com.soomsoom.backend.domain.achievement.model.entity

import com.soomsoom.backend.domain.achievement.model.enums.ConditionType

class AchievementCondition(
    val id: Long,
    val type: ConditionType,
    val targetValue: Int,
)
