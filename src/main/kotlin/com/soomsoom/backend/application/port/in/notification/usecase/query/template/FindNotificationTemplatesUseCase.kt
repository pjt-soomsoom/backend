package com.soomsoom.backend.application.port.`in`.notification.usecase.query.template

import com.soomsoom.backend.application.port.`in`.notification.dto.NotificationTemplateDto

interface FindNotificationTemplatesUseCase {
    fun findAll(): List<NotificationTemplateDto>
    fun findById(id: Long): NotificationTemplateDto
}
