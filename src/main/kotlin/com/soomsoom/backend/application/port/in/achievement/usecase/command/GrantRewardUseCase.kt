package com.soomsoom.backend.application.port.`in`.achievement.usecase.command

interface GrantRewardUseCase {
    fun grantReward(userId: Long, achievementId: Long)
}
