package com.soomsoom.backend.adapter.`in`.web.api.notification.request

import com.soomsoom.backend.application.port.`in`.notification.command.RegisterUserDeviceCommand
import com.soomsoom.backend.domain.notification.model.enums.OSType
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull

@Schema(description = "사용자 디바이스 등록 요청")
data class RegisterUserDeviceRequest(
    @Schema(description = "FCM 토큰", example = "e-...")
    @field:NotBlank(message = "FCM 토큰은 비어있을 수 없습니다.")
    val fcmToken: String?,

    @Schema(description = "OS 타입", example = "ANDROID, IOS")
    @field:NotNull(message = "OS 타입은 필수입니다.")
    val osType: OSType?,
) {
    fun toCommand(userId: Long) = RegisterUserDeviceCommand(
        userId = userId,
        fcmToken = this.fcmToken!!,
        osType = this.osType!!
    )
}
