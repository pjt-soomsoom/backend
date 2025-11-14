package com.soomsoom.backend.application.port.`in`.rewardedad.dto

import com.soomsoom.backend.common.entity.enums.OSType

/**
 * 관리자용 API에서 RewardedAd 정보를 반환할 때 사용할 DTO
 */
data class RewardedAdDto(
    val id: Long,
    val title: String,
    val adUnitId: String,
    val rewardAmount: Int,
    val active: Boolean,
    val platform: OSType,
)
