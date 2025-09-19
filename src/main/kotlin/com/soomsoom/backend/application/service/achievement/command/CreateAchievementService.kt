package com.soomsoom.backend.application.service.achievement.command

import com.soomsoom.backend.application.port.`in`.achievement.command.CreateAchievementCommand
import com.soomsoom.backend.application.port.`in`.achievement.dto.AchievementDto
import com.soomsoom.backend.application.port.`in`.achievement.usecase.command.CreateAchievementUseCase
import com.soomsoom.backend.application.port.out.achievement.AchievementPort
import com.soomsoom.backend.domain.achievement.model.aggregate.Achievement
import com.soomsoom.backend.domain.achievement.model.entity.AchievementCondition
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
            phrase = command.phrase,
            grade = command.grade,
            category = command.category,
            unlockedDisplayInfo = command.unlockedDisplayInfo,
            reward = command.reward
        )

        val conditions = command.conditions.map { conditionCommand ->
            AchievementCondition(
                id = 0L,
                type = conditionCommand.type,
                targetValue = conditionCommand.targetValue
            )
        }
        achievement.conditions = conditions

        val savedAchievement = achievementPort.save(achievement)

        // 4. DTO로 변환하여 반환
        return AchievementDto.from(savedAchievement)
    }
}
