package com.soomsoom.backend.application.port.`in`.notification.dto

import com.soomsoom.backend.domain.notification.model.entity.MessageVariation

data class MessageVariationDto(
    val id: Long,
    val templateId: Long,
    val titleTemplate: String,
    val bodyTemplate: String,
    val isActive: Boolean,
)

fun MessageVariation.toDto(): MessageVariationDto {
    return MessageVariationDto(
        id = this.id,
        templateId = this.notificationTemplate.id,
        titleTemplate = this.titleTemplate,
        bodyTemplate = this.bodyTemplate,
        isActive = this.active
    )
}
