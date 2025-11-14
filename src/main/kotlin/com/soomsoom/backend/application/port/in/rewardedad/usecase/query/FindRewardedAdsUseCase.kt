package com.soomsoom.backend.application.port.`in`.rewardedad.usecase.query

import com.soomsoom.backend.application.port.`in`.rewardedad.dto.RewardedAdStatusDto
import com.soomsoom.backend.common.entity.enums.OSType

interface FindRewardedAdsUseCase {
    fun findRewardedAds(userId: Long, platform: OSType): List<RewardedAdStatusDto>
}
