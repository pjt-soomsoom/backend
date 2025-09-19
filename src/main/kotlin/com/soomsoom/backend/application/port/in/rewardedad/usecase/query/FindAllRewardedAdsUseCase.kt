package com.soomsoom.backend.application.port.`in`.rewardedad.usecase.query

import com.soomsoom.backend.application.port.`in`.rewardedad.dto.RewardedAdDto

interface FindAllRewardedAdsUseCase {
    fun findAll(): List<RewardedAdDto>
}
