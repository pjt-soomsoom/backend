package com.soomsoom.backend.application.port.`in`.achievement.usecase.query

import com.soomsoom.backend.application.port.`in`.achievement.dto.FindMyAchievementsResult
import com.soomsoom.backend.domain.achievement.model.AchievementStatusFilter
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface FindMyAchievementsUseCase {
    fun find(userId: Long, pageable: Pageable, statusFilter: AchievementStatusFilter): Page<FindMyAchievementsResult>
}
