package com.soomsoom.backend.application.port.out.achievement.dto

import com.soomsoom.backend.domain.achievement.model.Achievement
import com.soomsoom.backend.domain.achievement.model.UserAchieved
import com.soomsoom.backend.domain.achievement.model.UserProgress

data class AchievementDetailsDto(
    val achievement: Achievement,
    val userAchieved: UserAchieved?,
    val userProgress: UserProgress?,
    val targetValue: Int,
)
