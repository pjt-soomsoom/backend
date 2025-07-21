package com.soomsoom.backend.adapter.`in`.web.api.auth.request

import com.soomsoom.backend.application.port.`in`.auth.command.AdminSignUpCommand
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

data class AdminSignUpRequest(
    @field:NotBlank(message = "username은 필수입니다.")
    @field:Size(min = 5, message = "username은 5글자 이상이어야 합니다.")
    val username: String,

    @field:NotBlank(message = "password는 필수입니다.")
    @field:Size(min = 5, message = "password는 5글자 이상이어야 합니다.")
    val password: String,
)

fun AdminSignUpRequest.toCommand() = AdminSignUpCommand(username, password)
