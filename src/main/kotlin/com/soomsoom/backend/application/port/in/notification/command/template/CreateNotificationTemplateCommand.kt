package com.soomsoom.backend.application.port.`in`.notification.command.template

import com.soomsoom.backend.domain.notification.model.enums.NotificationType

data class CreateNotificationTemplateCommand(
    val type: NotificationType,
    val description: String,
    val triggerCondition: Int?,
)
