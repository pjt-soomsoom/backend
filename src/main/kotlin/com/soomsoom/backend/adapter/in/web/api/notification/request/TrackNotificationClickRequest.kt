package com.soomsoom.backend.adapter.`in`.web.api.notification.request

import com.soomsoom.backend.application.port.`in`.notification.command.TrackNotificationClickCommand
import jakarta.validation.constraints.NotNull

data class TrackNotificationClickRequest(
    @field:NotNull(message = "히스토리 ID는 필수입니다.")
    val historyId: Long?,
) {
    fun toCommand() = TrackNotificationClickCommand(historyId = this.historyId!!)
}
