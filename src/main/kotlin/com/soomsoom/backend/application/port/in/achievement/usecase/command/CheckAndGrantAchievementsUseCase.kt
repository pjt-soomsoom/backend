package com.soomsoom.backend.application.port.`in`.achievement.usecase.command

import com.soomsoom.backend.domain.achievement.model.ConditionType

interface CheckAndGrantAchievementsUseCase {
    fun checkAndGrant(userId: Long, type: ConditionType)
}
