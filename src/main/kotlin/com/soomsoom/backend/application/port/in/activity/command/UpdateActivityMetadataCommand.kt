package com.soomsoom.backend.application.port.`in`.activity.command

import com.soomsoom.backend.domain.activity.model.enums.ActivityCategory

data class UpdateActivityMetadataCommand(
    val userId: Long,
    val activityId: Long,
    val title: String,
    val descriptions: List<String>,
    val category: ActivityCategory,
    val completionEffectTexts: List<String>,
)
