package com.soomsoom.backend.adapter.`in`.web.api.auth.request

import com.soomsoom.backend.application.port.`in`.auth.command.AdminLoginCommand
import jakarta.validation.constraints.NotBlank

data class AdminLoginRequest(
    @field:NotBlank(message = "username은 필수입니다.")
    val username: String,
    @field:NotBlank(message = "password는 필수입니다.")
    val password: String,
)

fun AdminLoginRequest.toCommand() = AdminLoginCommand(
    username,
    password
)
