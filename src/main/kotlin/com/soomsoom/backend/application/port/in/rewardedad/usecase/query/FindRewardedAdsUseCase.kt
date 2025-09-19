package com.soomsoom.backend.application.port.`in`.rewardedad.usecase.query

import com.soomsoom.backend.application.port.`in`.rewardedad.dto.RewardedAdStatusDto

interface FindRewardedAdsUseCase {
    fun findRewardedAds(userId: Long): List<RewardedAdStatusDto>
}
