package com.soomsoom.backend.application.port.`in`.follow.dto

data class ToggleFollowResult(
    val followeeId: Long,
    val isFollowing: Boolean,
)
