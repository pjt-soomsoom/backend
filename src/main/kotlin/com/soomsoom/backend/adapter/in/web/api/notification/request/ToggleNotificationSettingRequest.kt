package com.soomsoom.backend.adapter.`in`.web.api.notification.request

import com.soomsoom.backend.application.port.`in`.notification.command.ToggleNotificationSettingCommand
import com.soomsoom.backend.domain.notification.model.enums.NotificationType
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotNull

@Schema(description = "알림 설정 토글 요청")
data class ToggleNotificationSettingRequest(
    @Schema(description = "활성화 여부", example = "true")
    @field:NotNull(message = "활성화 여부는 필수입니다.")
    val enabled: Boolean?,
) {
    fun toCommand(userId: Long, type: NotificationType) = ToggleNotificationSettingCommand(
        userId = userId,
        type = type,
        enabled = this.enabled!!
    )
}
