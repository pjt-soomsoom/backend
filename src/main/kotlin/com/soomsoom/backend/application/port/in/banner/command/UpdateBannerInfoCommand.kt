package com.soomsoom.backend.application.port.`in`.banner.command

data class UpdateBannerInfoCommand(
    val bannerId: Long,
    val description: String,
    val buttonText: String,
    val linkedActivityId: Long,
    val isActive: Boolean,
    val displayOrder: Int?,
)
