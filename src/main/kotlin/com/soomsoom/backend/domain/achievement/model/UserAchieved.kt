package com.soomsoom.backend.domain.achievement.model

import java.time.LocalDateTime

class UserAchieved(
    val id: Long?,
    val userId: Long,
    val achievementId: Long,
    val achievedAt: LocalDateTime,
)
