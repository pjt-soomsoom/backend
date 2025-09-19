package com.soomsoom.backend.adapter.`in`.web.api.rewardedad.request

import com.soomsoom.backend.application.port.`in`.rewardedad.command.CreateRewardedAdCommand
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Positive

@Schema(description = "보상형 광고 생성 요청")
data class CreateRewardedAdRequest(
    @Schema(description = "광고 제목", example = "매일 하트 받기")
    @field:NotBlank
    val title: String,

    @Schema(description = "Google AdMob 광고 단위 ID", example = "ca-app-pub-3940256099942544/5224354917")
    @field:NotBlank
    val adUnitId: String,

    @Schema(description = "보상 포인트 양", example = "10")
    @field:Positive
    val rewardAmount: Int,
) {
    fun toCommand(): CreateRewardedAdCommand {
        return CreateRewardedAdCommand(
            title = this.title,
            adUnitId = this.adUnitId,
            rewardAmount = this.rewardAmount
        )
    }
}
