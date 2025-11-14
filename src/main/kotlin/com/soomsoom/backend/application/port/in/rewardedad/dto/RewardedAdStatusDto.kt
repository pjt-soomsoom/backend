package com.soomsoom.backend.application.port.`in`.rewardedad.dto

import com.soomsoom.backend.common.entity.enums.OSType

data class RewardedAdStatusDto(
    val id: Long,
    val title: String,
    val adUnitId: String,
    val rewardAmount: Int,
    val watchedToday: Boolean,
    val platform: OSType,
)
