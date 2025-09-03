package com.soomsoom.backend.application.port.`in`.achievement.usecase.command

import com.soomsoom.backend.application.port.`in`.achievement.command.UpdateAchievementCommand
import com.soomsoom.backend.application.port.`in`.achievement.dto.AchievementDto

interface UpdateAchievementUseCase {
    fun update(command: UpdateAchievementCommand): AchievementDto
}
