package com.soomsoom.backend.application.port.`in`.mission.dto

import com.soomsoom.backend.domain.mission.model.vo.MissionReward

data class MissionRewardUserDto(
    val points: Int?,
    val itemId: Long?,
) {
    companion object {
        fun from(reward: MissionReward): MissionRewardUserDto {
            return MissionRewardUserDto(
                points = reward.points,
                itemId = reward.itemId
            )
        }
    }
}
