package com.soomsoom.backend.application.port.`in`.notification.command.template

import com.soomsoom.backend.domain.notification.model.enums.NotificationType

data class UpdateNotificationTemplateCommand(
    val id: Long,
    val type: NotificationType,
    val description: String,
    val isActive: Boolean,
    val triggerCondition: Int?,
)
