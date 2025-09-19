package com.soomsoom.backend.application.port.`in`.rewardedad.command

data class UpdateRewardedAdCommand(
    val id: Long,
    val title: String,
    val rewardAmount: Int,
    val active: Boolean,
)
