package com.soomsoom.backend.application.port.`in`.rewardedad.command

data class CreateRewardedAdCommand(
    val title: String,
    val adUnitId: String,
    val rewardAmount: Int,
)
