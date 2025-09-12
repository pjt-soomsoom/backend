package com.soomsoom.backend.adapter.`in`.web.api.auth.request

import com.soomsoom.backend.application.port.`in`.auth.command.DeviceAuthenticationCommand
import jakarta.validation.constraints.NotBlank

data class DeviceAuthenticationRequest(
    @field:NotBlank(message = "deviceId는 필수 입력 항목입니다.")
    val deviceId: String,
) {
    fun toCommand(): DeviceAuthenticationCommand {
        return DeviceAuthenticationCommand(deviceId = this.deviceId)
    }
}
