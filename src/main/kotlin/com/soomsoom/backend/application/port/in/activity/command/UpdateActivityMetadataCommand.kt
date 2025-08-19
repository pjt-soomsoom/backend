package com.soomsoom.backend.application.port.`in`.activity.command

data class UpdateActivityMetadataCommand(
    val activityId: Long,
    val title: String,
    val descriptions: List<String>,
)
