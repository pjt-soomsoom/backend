package com.soomsoom.backend.adapter.`in`.web.api.auth.request

import com.soomsoom.backend.application.port.`in`.auth.command.SocialAuthenticationCommand
import com.soomsoom.backend.domain.user.model.enums.SocialProvider
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull

data class SocialAuthenticationRequest(
    @field:NotNull(message = "provider는 필수 입력 항목입니다.")
    val provider: SocialProvider,

    @field:NotBlank(message = "providerToken은 필수 입력 항목입니다.")
    val providerToken: String,

    @field:NotBlank(message = "deviceId는 필수 입력 항목입니다.")
    val deviceId: String,
) {
    fun toCommand(): SocialAuthenticationCommand {
        return SocialAuthenticationCommand(
            provider = this.provider,
            providerToken = this.providerToken,
            deviceId = this.deviceId,
        )
    }
}
