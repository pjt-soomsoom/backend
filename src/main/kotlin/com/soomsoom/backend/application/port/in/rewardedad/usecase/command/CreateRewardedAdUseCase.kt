package com.soomsoom.backend.application.port.`in`.rewardedad.usecase.command

import com.soomsoom.backend.application.port.`in`.rewardedad.command.CreateRewardedAdCommand
import com.soomsoom.backend.application.port.`in`.rewardedad.dto.RewardedAdDto

interface CreateRewardedAdUseCase {
    fun command(command: CreateRewardedAdCommand): RewardedAdDto
}
