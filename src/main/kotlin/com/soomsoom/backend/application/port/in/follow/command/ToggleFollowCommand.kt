package com.soomsoom.backend.application.port.`in`.follow.command

data class ToggleFollowCommand(
    val followerId: Long,
    val followeeId: Long,
)
