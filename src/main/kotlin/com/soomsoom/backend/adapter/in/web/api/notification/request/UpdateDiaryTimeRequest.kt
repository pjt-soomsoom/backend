package com.soomsoom.backend.adapter.`in`.web.api.notification.request

import com.soomsoom.backend.application.port.`in`.notification.command.UpdateDiaryTimeCommand
import jakarta.validation.constraints.NotNull
import java.time.LocalTime

data class UpdateDiaryTimeRequest(
    @field:NotNull(message = "알림 시간은 필수입니다.")
    val time: LocalTime?,
) {
    fun toCommand(userId: Long) = UpdateDiaryTimeCommand(
        userId = userId,
        diaryNotificationTime = this.time!!
    )
}
