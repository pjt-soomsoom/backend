package com.soomsoom.backend.application.port.`in`.mission.usecase.command

import com.soomsoom.backend.application.port.`in`.mission.command.ClaimMissionRewardCommand
import com.soomsoom.backend.application.port.`in`.mission.dto.ClaimMissionRewardResult
/**
 * 사용자가 수동으로 미션 보상을 수령하는 유스케이스.
 * 이 유스케이스는 '수동 수령(MANUAL)' 타입의 미션에 대해서만 동작
 */
interface ClaimMissionRewardUseCase {
    fun claimReward(command: ClaimMissionRewardCommand): ClaimMissionRewardResult
}
