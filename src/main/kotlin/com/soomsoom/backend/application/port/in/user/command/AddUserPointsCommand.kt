package com.soomsoom.backend.application.port.`in`.user.command

data class AddUserPointsCommand(
    val userId: Long,
    val pointsToAdd: Int,
)
