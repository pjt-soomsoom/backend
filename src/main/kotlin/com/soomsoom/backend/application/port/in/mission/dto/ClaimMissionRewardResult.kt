package com.soomsoom.backend.application.port.`in`.mission.dto

/**
 * 보상 수령 유스케이스(`ClaimMissionRewardUseCase`)의 실행 결과를 담는 DTO 입니다.
 *
 * @property claimedReward 수령한 보상에 대한 상세 정보
 */
data class ClaimMissionRewardResult(
    val claimedReward: MissionRewardUserDto,
)
