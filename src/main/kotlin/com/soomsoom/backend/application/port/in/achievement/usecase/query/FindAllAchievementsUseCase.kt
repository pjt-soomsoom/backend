package com.soomsoom.backend.application.port.`in`.achievement.usecase.query

import com.soomsoom.backend.application.port.`in`.achievement.dto.AchievementDto
import com.soomsoom.backend.application.port.`in`.achievement.query.FindAllAchievementsCriteria
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface FindAllAchievementsUseCase {
    fun findAll(criteria: FindAllAchievementsCriteria, pageable: Pageable): Page<AchievementDto>
}
