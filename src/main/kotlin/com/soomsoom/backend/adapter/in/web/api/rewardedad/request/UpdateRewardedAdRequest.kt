package com.soomsoom.backend.adapter.`in`.web.api.rewardedad.request

import com.soomsoom.backend.application.port.`in`.rewardedad.command.UpdateRewardedAdCommand
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Positive

@Schema(description = "보상형 광고 수정 요청")
data class UpdateRewardedAdRequest(
    @Schema(description = "광고 제목", example = "매일 하트 2배 받기")
    @field:NotBlank
    val title: String,

    @Schema(description = "보상 포인트 양", example = "20")
    @field:Positive
    val rewardAmount: Int,

    @Schema(description = "활성화 여부", example = "true")
    @field:NotNull
    val active: Boolean,
) {
    fun toCommand(id: Long): UpdateRewardedAdCommand {
        return UpdateRewardedAdCommand(
            id = id,
            title = this.title,
            rewardAmount = this.rewardAmount,
            active = this.active
        )
    }
}
