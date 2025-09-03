package com.soomsoom.backend.application.port.`in`.user.command

data class DeductUserPointsCommand(
    val userId: Long,
    val pointsToDeduct: Int,
)
