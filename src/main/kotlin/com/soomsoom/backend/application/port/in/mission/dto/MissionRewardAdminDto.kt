package com.soomsoom.backend.application.port.`in`.mission.dto

import com.soomsoom.backend.domain.mission.model.vo.MissionReward

/**
 * 미션 보상 정보를 담는 DTO 입니다. (관리자용 상세 정보)
 */
data class MissionRewardAdminDto(
    val points: Int?,
    val itemId: Long?,
    val notification: NotificationContentDto,
) {
    companion object {
        fun from(reward: MissionReward): MissionRewardAdminDto {
            return MissionRewardAdminDto(
                points = reward.points,
                itemId = reward.itemId,
                notification = NotificationContentDto.from(reward.notification)
            )
        }
    }
}
