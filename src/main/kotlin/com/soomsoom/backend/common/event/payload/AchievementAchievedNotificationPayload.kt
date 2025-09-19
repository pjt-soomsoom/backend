package com.soomsoom.backend.common.event.payload

import com.soomsoom.backend.common.event.NotificationPayload
import com.soomsoom.backend.domain.achievement.model.enums.AchievementGrade

data class AchievementAchievedNotificationPayload(
    val userId: Long,
    val achievementId: Long,
    val achievementName: String,
    val title: String,
    val body: String,
    val achievementGrade: AchievementGrade,
) : NotificationPayload
