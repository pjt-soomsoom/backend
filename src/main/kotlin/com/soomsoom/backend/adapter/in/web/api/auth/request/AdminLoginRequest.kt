package com.soomsoom.backend.adapter.`in`.web.api.auth.request

import com.soomsoom.backend.application.port.`in`.auth.command.AdminLoginCommand

data class AdminLoginRequest(
    val username: String,
    val password: String,
)

fun AdminLoginRequest.toCommand() = AdminLoginCommand(
    username,
    password,
)
