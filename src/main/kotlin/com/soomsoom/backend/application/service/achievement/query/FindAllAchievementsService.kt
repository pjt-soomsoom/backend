package com.soomsoom.backend.application.service.achievement.query

import com.soomsoom.backend.application.port.`in`.achievement.dto.AchievementDto
import com.soomsoom.backend.application.port.`in`.achievement.query.FindAllAchievementsCriteria
import com.soomsoom.backend.application.port.`in`.achievement.usecase.query.FindAllAchievementsUseCase
import com.soomsoom.backend.application.port.out.achievement.AchievementPort
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class FindAllAchievementsService(
    private val achievementPort: AchievementPort,
) : FindAllAchievementsUseCase {

    @PreAuthorize("hasRole('ADMIN')")
    override fun findAll(criteria: FindAllAchievementsCriteria, pageable: Pageable): Page<AchievementDto> {
        val achievementsPage = achievementPort.findAll(criteria, pageable)
        return achievementsPage.map { AchievementDto.from(it, it.conditions) }
    }
}
