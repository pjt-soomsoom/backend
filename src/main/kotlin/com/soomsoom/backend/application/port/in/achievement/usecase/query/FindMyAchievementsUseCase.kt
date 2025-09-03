package com.soomsoom.backend.application.port.`in`.achievement.usecase.query

import com.soomsoom.backend.application.port.`in`.achievement.dto.FindMyAchievementsResult
import com.soomsoom.backend.application.port.`in`.achievement.query.FindMyAchievementsCriteria
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface FindMyAchievementsUseCase {
    fun find(criteria: FindMyAchievementsCriteria, pageable: Pageable): Page<FindMyAchievementsResult>
}
