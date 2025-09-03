package com.soomsoom.backend.common.event.payload

import com.soomsoom.backend.common.event.NotificationPayload

data class AchievementAchievedNotificationPayload(
    val userId: Long,
    val achievementId: Long,
) : NotificationPayload
