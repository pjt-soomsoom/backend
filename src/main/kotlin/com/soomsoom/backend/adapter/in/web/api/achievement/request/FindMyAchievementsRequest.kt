package com.soomsoom.backend.adapter.`in`.web.api.achievement.request

import com.soomsoom.backend.domain.achievement.model.AchievementStatusFilter

class FindMyAchievementsRequest(
    val userId: Long?,
    val status: AchievementStatusFilter?
)
