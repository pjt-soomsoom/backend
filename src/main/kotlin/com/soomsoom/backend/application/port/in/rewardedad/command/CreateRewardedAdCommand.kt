package com.soomsoom.backend.application.port.`in`.rewardedad.command

import com.soomsoom.backend.common.entity.enums.OSType

data class CreateRewardedAdCommand(
    val title: String,
    val adUnitId: String,
    val rewardAmount: Int,
    val platform: OSType,
)
