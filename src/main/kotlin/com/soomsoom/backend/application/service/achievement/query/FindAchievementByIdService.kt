package com.soomsoom.backend.application.service.achievement.query

import com.soomsoom.backend.application.port.`in`.achievement.dto.AchievementDto
import com.soomsoom.backend.application.port.`in`.achievement.usecase.query.FindAchievementByIdUseCase
import com.soomsoom.backend.application.port.out.achievement.AchievementPort
import com.soomsoom.backend.common.exception.SoomSoomException
import com.soomsoom.backend.domain.achievement.AchievementErrorCode
import com.soomsoom.backend.domain.common.DeletionStatus
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class FindAchievementByIdService(
    private val achievementPort: AchievementPort,
) : FindAchievementByIdUseCase {

    @PreAuthorize("hasRole('ADMIN')")
    override fun findById(achievementId: Long): AchievementDto {
        val achievement = achievementPort.findById(achievementId, DeletionStatus.ALL)
            ?: throw SoomSoomException(AchievementErrorCode.NOT_FOUND)

        return AchievementDto.from(achievement, achievement.conditions)
    }
}
