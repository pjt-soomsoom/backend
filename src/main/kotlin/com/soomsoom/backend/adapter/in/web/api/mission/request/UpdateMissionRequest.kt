package com.soomsoom.backend.adapter.`in`.web.api.mission.request

import com.soomsoom.backend.application.port.`in`.mission.command.UpdateMissionCommand
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

@Schema(description = "미션 수정 요청 DTO")
data class UpdateMissionRequest(
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
    val targetValue: Int?,

    @field:Schema(description = "미션 달성 시 알림 정보")
    @field:NotNull
    @field:Valid
    val completionNotification: CreateMissionRequest.NotificationRequest?,

    @field:Schema(description = "미션 보상 정보")
    @field:NotNull
    @field:Valid
    val reward: CreateMissionRequest.RewardRequest?,

    @field:Schema(description = "미션 반복 주기")
    @field:NotNull
    val repeatableType: RepeatableType?,

    @field:Schema(description = "보상 수령 방식")
    @field:NotNull
    val claimType: ClaimType?,
) {
    fun toCommand(missionId: Long): UpdateMissionCommand {
        return UpdateMissionCommand(
            missionId = missionId,
            type = this.type!!,
            title = this.title,
            description = this.description,
            targetValue = this.targetValue!!,
            completionNotification = NotificationContent(
                title = this.completionNotification!!.title,
                body = this.completionNotification.body
            ),
            reward = MissionReward(
                points = this.reward!!.points,
                itemId = this.reward.itemId,
                notification = NotificationContent(
                    title = this.reward.notification!!.title,
                    body = this.reward.notification.body
                )
            ),
            repeatableType = this.repeatableType!!,
            claimType = this.claimType!!
        )
    }
}
