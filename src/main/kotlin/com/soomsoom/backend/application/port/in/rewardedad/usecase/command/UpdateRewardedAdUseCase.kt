package com.soomsoom.backend.application.port.`in`.rewardedad.usecase.command

import com.soomsoom.backend.application.port.`in`.rewardedad.command.UpdateRewardedAdCommand
import com.soomsoom.backend.application.port.`in`.rewardedad.dto.RewardedAdDto

interface UpdateRewardedAdUseCase {
    fun command(command: UpdateRewardedAdCommand): RewardedAdDto
}
