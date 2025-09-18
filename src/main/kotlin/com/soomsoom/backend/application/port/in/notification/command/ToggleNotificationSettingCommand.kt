package com.soomsoom.backend.application.port.`in`.notification.command

import com.soomsoom.backend.domain.notification.model.enums.NotificationType

data class ToggleNotificationSettingCommand(
    val userId: Long,
    val type: NotificationType,
    val enabled: Boolean,
)
