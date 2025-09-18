package com.soomsoom.backend.application.port.`in`.notification.command.message

data class UpdateMessageVariationCommand(
    val id: Long,
    val titleTemplate: String,
    val bodyTemplate: String,
    val isActive: Boolean,
)
