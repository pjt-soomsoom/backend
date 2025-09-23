package com.soomsoom.backend.application.port.`in`.notification.dto

import com.soomsoom.backend.domain.notification.model.entity.NotificationTemplate
import com.soomsoom.backend.domain.notification.model.enums.NotificationType

data class NotificationTemplateDto(
    val id: Long,
    val type: NotificationType,
    val description: String,
    val isActive: Boolean,
    val triggerCondition: Int?,
    val variations: List<MessageVariationDto>,
)

fun NotificationTemplate.toDto(): NotificationTemplateDto {
    return NotificationTemplateDto(
        id = this.id,
        type = this.type,
        description = this.description,
        isActive = this.active,
        triggerCondition = this.triggerCondition,
        variations = this.variations.map { it.toDto() }
    )
}
