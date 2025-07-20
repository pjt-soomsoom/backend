package com.soomsoom.backend.application.port.`in`.auth.command

data class AdminSignUpCommand(
    val username: String,
    val password: String,
)
