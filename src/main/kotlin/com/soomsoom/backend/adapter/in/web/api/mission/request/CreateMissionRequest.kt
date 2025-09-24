package com.soomsoom.backend.adapter.`in`.web.api.mission.request

import com.soomsoom.backend.application.port.`in`.mission.command.CreateMissionCommand
import com.soomsoom.backend.domain.mission.model.enums.ClaimType
import com.soomsoom.backend.domain.mission.model.enums.MissionType
import com.soomsoom.backend.domain.mission.model.enums.RepeatableType
import com.soomsoom.backend.domain.mission.model.vo.MissionReward
import com.soomsoom.backend.domain.mission.model.vo.NotificationContent
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.Valid
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Positive

data class CreateMissionRequest(
    @field:Schema(description = "미션 타입")
    @field:NotNull
    val type: MissionType?,

    @field:Schema(description = "미션 제목")
    @field:NotBlank
    val title: String,

    @field:Schema(description = "미션 설명")
    @field:NotBlank
    val description: String,

    @field:Schema(description = "미션 목표값")
    @field:NotNull
    @field:Positive
    val targetValue: Int,

    @field:Schema(description = "미션 달성 시 알림 정보")
    @field:NotNull
    @field:Valid
    val completionNotification: NotificationRequest?,

    @field:Schema(description = "미션 보상 정보")
    @field:NotNull
    @field:Valid
    val reward: RewardRequest?,

    @field:Schema(description = "미션 반복 주기")
    @field:NotNull
    val repeatableType: RepeatableType,

    @field:Schema(description = "보상 수령 방식")
    @field:NotNull
    val claimType: ClaimType,
) {
    @Schema(description = "알림 메시지 DTO")
    data class NotificationRequest(
        @field:Schema(description = "알림 제목")
        @field:NotBlank
        val title: String,

        @field:Schema(description = "알림 본문")
        @field:NotBlank
        val body: String,
    )

    @Schema(description = "보상 정보 DTO")
    data class RewardRequest(
        @field:Schema(description = "보상 포인트 (없으면 null)")
        val points: Int?,

        @field:Schema(description = "보상 아이템 ID (없으면 null)")
        val itemId: Long?,

        @field:Schema(description = "보상 지급 시 알림 정보")
        @field:NotNull
        @field:Valid
        val notification: NotificationRequest?,
    )

    fun toCommand(): CreateMissionCommand {
        val finalCompletionNotification = requireNotNull(this.completionNotification)
        val finalReward = requireNotNull(this.reward)
        val finalRewardNotification = requireNotNull(finalReward.notification)

        return CreateMissionCommand(
            type = this.type!!,
            title = this.title,
            description = this.description,
            targetValue = this.targetValue,
            completionNotification = NotificationContent(
                title = finalCompletionNotification.title,
                body = finalCompletionNotification.body
            ),
            reward = MissionReward(
                points = finalReward.points,
                itemId = finalReward.itemId,
                notification = NotificationContent(
                    title = finalRewardNotification.title,
                    body = finalRewardNotification.body
                )
            ),
            repeatableType = this.repeatableType,
            claimType = this.claimType
        )
    }
}
