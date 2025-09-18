package com.soomsoom.backend.application.port.`in`.notification.usecase.query

import com.soomsoom.backend.application.port.`in`.notification.dto.NotificationSettingsDto

interface FindNotificationSettingsUseCase {
    fun findByUserId(userId: Long): NotificationSettingsDto
}
