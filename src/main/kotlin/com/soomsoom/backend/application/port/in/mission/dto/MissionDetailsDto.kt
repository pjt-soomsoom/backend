package com.soomsoom.backend.application.port.`in`.mission.dto

import com.soomsoom.backend.domain.mission.model.aggregate.Mission
import com.soomsoom.backend.domain.mission.model.enums.ClaimType
import com.soomsoom.backend.domain.mission.model.enums.MissionType
import com.soomsoom.backend.domain.mission.model.enums.RepeatableType

/**
 * 관리자 페이지에서 미션의 모든 상세 정보를 조회할 때 사용하는 DTO 입니다.
 * 사용자에게 보여주는 `MissionDto`와 달리 모든 내부 설정값을 포함합니다.
 */
data class MissionDetailsDto(
    val missionId: Long,
    val type: MissionType,
    val title: String,
    val description: String,
    val condition: MissionConditionDto,
    val completionNotification: NotificationContentDto,
    val reward: MissionRewardAdminDto,
    val repeatableType: RepeatableType,
    val claimType: ClaimType,
    val isDeleted: Boolean,
) {
    companion object {
        fun from(mission: Mission): MissionDetailsDto {
            return MissionDetailsDto(
                missionId = mission.id,
                type = mission.type,
                title = mission.title,
                description = mission.description,
                condition = MissionConditionDto.from(mission.condition),
                completionNotification = NotificationContentDto.from(mission.completionNotification),
                reward = MissionRewardAdminDto.from(mission.reward),
                repeatableType = mission.repeatableType,
                claimType = mission.claimType,
                isDeleted = mission.isDeleted()
            )
        }
    }
}
