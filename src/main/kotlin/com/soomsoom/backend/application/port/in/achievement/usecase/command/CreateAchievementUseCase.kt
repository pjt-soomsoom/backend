package com.soomsoom.backend.application.port.`in`.achievement.usecase.command

import com.soomsoom.backend.application.port.`in`.achievement.command.CreateAchievementCommand
import com.soomsoom.backend.application.port.`in`.achievement.dto.AchievementDto

interface CreateAchievementUseCase {
    fun create(command: CreateAchievementCommand): AchievementDto
}
