package com.soomsoom.backend.application.port.`in`.achievement.usecase.query

import com.soomsoom.backend.application.port.`in`.achievement.dto.AchievementDto

interface FindAchievementByIdUseCase {
    fun findById(achievementId: Long): AchievementDto
}
