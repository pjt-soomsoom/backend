package com.soomsoom.backend.application.port.`in`.activity.command

data class UpdateActivityMetadataCommand(
    val userId: Long,
    val activityId: Long,
    val title: String,
    val descriptions: List<String>,
)
