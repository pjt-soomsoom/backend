package com.soomsoom.backend.application.service.achievement.query

import com.soomsoom.backend.application.port.`in`.achievement.dto.FindMyAchievementsResult
import com.soomsoom.backend.application.port.`in`.achievement.usecase.query.FindMyAchievementsUseCase
import com.soomsoom.backend.application.port.out.achievement.AchievementPort
import com.soomsoom.backend.domain.achievement.model.AchievementStatusFilter
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class FindMyAchievementsService(
    private val achievementPort: AchievementPort,
) : FindMyAchievementsUseCase {

    @PreAuthorize("hasRole('ADMIN') or #userId == authentication.principal.id")
    override fun find(userId: Long, pageable: Pageable, statusFilter: AchievementStatusFilter): Page<FindMyAchievementsResult> {
        val achievementWithProgressPage = achievementPort.findAchievementsWithProgress(userId, pageable, statusFilter)

        return achievementWithProgressPage.map { dto ->
            FindMyAchievementsResult.of(
                achievement = dto.achievement,
                userAchieved = dto.userAchieved,
                userProgress = dto.userProgress,
                targetValue = dto.targetValue
            )
        }
    }
}
