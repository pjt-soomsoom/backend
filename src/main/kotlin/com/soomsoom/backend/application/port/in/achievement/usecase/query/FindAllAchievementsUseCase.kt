package com.soomsoom.backend.application.port.`in`.achievement.usecase.query

import com.soomsoom.backend.application.port.`in`.achievement.dto.AchievementDto
import org.springframework.data.domain.Page
import java.awt.print.Pageable

interface FindAllAchievementsUseCase {
    fun findAll(pageable: Pageable): Page<AchievementDto>
}
