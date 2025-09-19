package com.soomsoom.backend.application.port.out.achievement.dto

import com.soomsoom.backend.domain.achievement.model.aggregate.Achievement
import com.soomsoom.backend.domain.achievement.model.entity.UserAchieved
import com.soomsoom.backend.domain.achievement.model.entity.UserProgress

data class AchievementDetailsDto(
    val achievement: Achievement,
    val userAchieved: UserAchieved?,
    val userProgress: UserProgress?,
    val targetValue: Int,
)
