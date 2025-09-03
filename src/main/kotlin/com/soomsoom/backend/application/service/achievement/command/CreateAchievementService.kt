package com.soomsoom.backend.application.service.achievement.command

import com.soomsoom.backend.application.port.`in`.achievement.command.CreateAchievementCommand
import com.soomsoom.backend.application.port.`in`.achievement.dto.AchievementDto
import com.soomsoom.backend.application.port.`in`.achievement.usecase.command.CreateAchievementUseCase
import com.soomsoom.backend.application.port.out.achievement.AchievementPort
import com.soomsoom.backend.domain.achievement.model.Achievement
import com.soomsoom.backend.domain.achievement.model.AchievementCondition
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class CreateAchievementService(
    private val achievementPort: AchievementPort,
) : CreateAchievementUseCase {

    @PreAuthorize("hasRole('ADMIN')")
    override fun create(command: CreateAchievementCommand): AchievementDto {
        val achievement = Achievement.create(
            name = command.name,
            description = command.description,
            phrase = command.phrase,
            grade = command.grade,
            category = command.category,
            rewardPoints = command.rewardPoints,
            rewardItemId = command.rewardItemId
        )

        val savedAchievement = achievementPort.save(achievement)

        val conditions = command.conditions.map { conditionCommand ->
            AchievementCondition(
                id = 0L,
                achievementId = savedAchievement.id,
                type = conditionCommand.type,
                targetValue = conditionCommand.targetValue
            )
        }

        val savedConditions = achievementPort.saveConditions(conditions)

        return AchievementDto.from(savedAchievement, savedConditions)
    }
}
