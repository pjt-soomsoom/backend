package com.soomsoom.backend.adapter.`in`.web.api.notification.request

import com.soomsoom.backend.application.port.`in`.notification.command.UnregisterUserDeviceCommand
import jakarta.validation.constraints.NotBlank

data class UnregisterUserDeviceRequest(
    @field:NotBlank(message = "FCM 토큰은 비어있을 수 없습니다.")
    val fcmToken: String?,
) {
    fun toCommand() = UnregisterUserDeviceCommand(fcmToken = this.fcmToken!!)
}
