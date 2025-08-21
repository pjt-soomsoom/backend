package com.soomsoom.backend.application.port.`in`.favorite.command

data class ToggleFavoriteCommand(
    val userId: Long,
    val activityId: Long,
)
