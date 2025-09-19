package com.soomsoom.backend.application.port.`in`.rewardedad.dto

data class RewardedAdStatusDto(
    val id: Long,
    val title: String,
    val adUnitId: String,
    val rewardAmount: Int,
    val watchedToday: Boolean,
)
