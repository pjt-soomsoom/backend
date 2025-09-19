package com.soomsoom.backend.adapter.`in`.web.api.rewardedad

import com.soomsoom.backend.adapter.`in`.security.service.CustomUserDetails
import com.soomsoom.backend.application.port.`in`.rewardedad.dto.RewardedAdStatusDto
import com.soomsoom.backend.application.port.`in`.rewardedad.usecase.query.FindRewardedAdsUseCase
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@Tag(name = "보상형 광고", description = "보상형 광고 관련 API")
@RestController
@RequestMapping("/rewarded-ads")
class RewardedAdController(
    private val findRewardedAdsUseCase: FindRewardedAdsUseCase,
) {
    @Operation(summary = "내 보상형 광고 목록 조회", description = "사용자가 시청할 수 있는 활성화된 광고 목록과 오늘의 시청 완료 여부를 조회합니다.")
    @GetMapping("/me")
    fun findMyRewardedAds(
        @AuthenticationPrincipal userDetails: CustomUserDetails,
    ): List<RewardedAdStatusDto> {
        return findRewardedAdsUseCase.findRewardedAds(userDetails.id)
    }
}
