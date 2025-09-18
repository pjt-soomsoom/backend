package com.soomsoom.backend.application.port.`in`.notification.dto

import java.time.LocalTime

data class NotificationSettingsDto(
    val diaryNotificationEnabled: Boolean,
    val diaryNotificationTime: LocalTime,
    val soomsoomNewsNotificationEnabled: Boolean,
    val reEngagementNotificationEnabled: Boolean,
)
