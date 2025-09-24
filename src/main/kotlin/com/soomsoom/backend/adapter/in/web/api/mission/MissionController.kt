package com.soomsoom.backend.adapter.`in`.web.api.mission

import com.soomsoom.backend.adapter.`in`.security.service.CustomUserDetails
import com.soomsoom.backend.application.port.`in`.mission.command.ClaimMissionRewardCommand
import com.soomsoom.backend.application.port.`in`.mission.dto.ClaimMissionRewardResult
import com.soomsoom.backend.application.port.`in`.mission.usecase.command.ClaimMissionRewardUseCase
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/missions")
@Tag(name = "미션", description = "미션 보상 수령 관련 API")
class MissionController(
    private val claimMissionRewardUseCase: ClaimMissionRewardUseCase,
) {
    @PostMapping("/{missionId}/claim")
    @Operation(summary = "미션 보상 수령")
    fun claimReward(
        @PathVariable missionId: Long,
        @AuthenticationPrincipal user: CustomUserDetails,
    ): ClaimMissionRewardResult {
        val command = ClaimMissionRewardCommand(user.id, missionId)
        return claimMissionRewardUseCase.claimReward(command)
    }
}
