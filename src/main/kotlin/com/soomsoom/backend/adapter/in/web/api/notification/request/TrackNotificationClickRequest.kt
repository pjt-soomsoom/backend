package com.soomsoom.backend.adapter.`in`.web.api.notification.request

import com.soomsoom.backend.application.port.`in`.notification.command.TrackNotificationClickCommand
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotNull

@Schema(description = "알림 클릭 추적 요청")
data class TrackNotificationClickRequest(
    @Schema(description = "추적할 알림 히스토리의 ID")
    @field:NotNull(message = "히스토리 ID는 필수입니다.")
    val historyId: Long?,
) {
    fun toCommand() = TrackNotificationClickCommand(historyId = this.historyId!!)
}
