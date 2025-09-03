package com.soomsoom.backend.application.port.`in`.achievement.command

import com.soomsoom.backend.domain.achievement.model.AchievementCategory
import com.soomsoom.backend.domain.achievement.model.AchievementGrade

data class CreateAchievementCommand(
    val name: String,
    val description: String,
    val phrase: String?,
    val grade: AchievementGrade,
    val category: AchievementCategory,
    val rewardPoints: Int?,
    val rewardItemId: Long?,
)
