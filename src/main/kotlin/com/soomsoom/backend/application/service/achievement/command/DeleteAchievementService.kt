package com.soomsoom.backend.application.service.achievement.command

import com.soomsoom.backend.application.port.`in`.achievement.usecase.command.DeleteAchievementUseCase
import com.soomsoom.backend.application.port.out.achievement.AchievementPort
import com.soomsoom.backend.common.exception.SoomSoomException
import com.soomsoom.backend.domain.achievement.AchievementErrorCode
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class DeleteAchievementService(
    private val achievementPort: AchievementPort,
) : DeleteAchievementUseCase {

    @PreAuthorize("hasRole('ADMIN')")
    override fun delete(achievementId: Long) {
        val achievement = achievementPort.findById(achievementId)
            ?: throw SoomSoomException(AchievementErrorCode.NOT_FOUND)

        achievement.delete()
        achievementPort.save(achievement)
    }
}
