package com.soomsoom.backend.application.service.achievement.command

import com.soomsoom.backend.application.port.`in`.achievement.command.UpdateAchievementCommand
import com.soomsoom.backend.application.port.`in`.achievement.dto.AchievementDto
import com.soomsoom.backend.application.port.`in`.achievement.usecase.command.UpdateAchievementUseCase
import com.soomsoom.backend.application.port.out.achievement.AchievementPort
import com.soomsoom.backend.common.exception.SoomSoomException
import com.soomsoom.backend.domain.achievement.AchievementErrorCode
import com.soomsoom.backend.domain.achievement.model.entity.AchievementCondition
import com.soomsoom.backend.domain.common.DeletionStatus
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class UpdateAchievementService(
    private val achievementPort: AchievementPort,
) : UpdateAchievementUseCase {

    @PreAuthorize("hasRole('ADMIN')")
    override fun update(command: UpdateAchievementCommand): AchievementDto {
        val achievement = (
            achievementPort.findById(command.id, DeletionStatus.ALL)
                ?: throw SoomSoomException(AchievementErrorCode.NOT_FOUND)
            )

        achievement.update(
            name = command.name,
            phrase = command.phrase,
            grade = command.grade,
            category = command.category,
            unlockedDisplayInfo = command.unlockedDisplayInfo,
            reward = command.reward
        )

        // command.conditions가 null이 아닐 때만 조건 교체 로직 수행
        val finalConditions = command.conditions?.let { newConditionCommands ->
            achievementPort.deleteConditionsByAchievementId(command.id)
            val newConditions = newConditionCommands.map {
                AchievementCondition(
                    id = 0L,
                    type = it.type,
                    targetValue = it.targetValue
                )
            }
            newConditions
        } ?: achievementPort.findConditionsByAchievementId(command.id) // null이면 기존 조건들을 다시 조회
        achievement.conditions = finalConditions
        val updatedAchievement = achievementPort.save(achievement)
        return AchievementDto.from(updatedAchievement)
    }
}
