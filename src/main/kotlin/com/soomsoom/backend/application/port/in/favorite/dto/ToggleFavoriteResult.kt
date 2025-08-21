package com.soomsoom.backend.application.port.`in`.favorite.dto

data class ToggleFavoriteResult(
    val activityId: Long,
    val isFavorited: Boolean,
)
