package com.soomsoom.backend.application.port.`in`.rewardedad.command

import com.soomsoom.backend.common.entity.enums.OSType

data class UpdateRewardedAdCommand(
    val id: Long,
    val title: String,
    val rewardAmount: Int,
    val active: Boolean,
    val platform: OSType,
)
