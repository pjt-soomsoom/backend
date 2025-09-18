package com.soomsoom.backend.application.port.`in`.notification.command.message

data class AddMessageVariationCommand(
    val templateId: Long,
    val titleTemplate: String,
    val bodyTemplate: String,
)
