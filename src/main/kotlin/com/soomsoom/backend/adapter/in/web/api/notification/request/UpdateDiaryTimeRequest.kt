package com.soomsoom.backend.adapter.`in`.web.api.notification.request

import com.soomsoom.backend.application.port.`in`.notification.command.UpdateDiaryTimeCommand
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotNull
import java.time.LocalTime

@Schema(description = "일기 알림 시간 수정 요청")
data class UpdateDiaryTimeRequest(
    @Schema(description = "알림 시간 (HH:mm:ss)", example = "22:00:00")
    @field:NotNull(message = "알림 시간은 필수입니다.")
    val time: LocalTime?,
) {
    fun toCommand(userId: Long) = UpdateDiaryTimeCommand(
        userId = userId,
        diaryNotificationTime = this.time!!
    )
}
