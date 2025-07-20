package com.soomsoom.backend.adapter.`in`.web.api.auth.request

import com.soomsoom.backend.application.port.`in`.auth.command.AdminSignUpCommand

data class AdminSignUpRequest(
    val username: String,
    val password: String,
)

fun AdminSignUpRequest.toCommand() = AdminSignUpCommand(username, password)
