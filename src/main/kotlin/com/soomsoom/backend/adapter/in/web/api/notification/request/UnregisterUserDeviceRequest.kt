package com.soomsoom.backend.adapter.`in`.web.api.notification.request

import com.soomsoom.backend.application.port.`in`.notification.command.UnregisterUserDeviceCommand
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotBlank

@Schema(description = "사용자 디바이스 등록 해지 요청")
data class UnregisterUserDeviceRequest(
    @Schema(description = "FCM 토큰", example = "e-...")
    @field:NotBlank(message = "FCM 토큰은 비어있을 수 없습니다.")
    val fcmToken: String?,
) {
    fun toCommand() = UnregisterUserDeviceCommand(fcmToken = this.fcmToken!!)
}
