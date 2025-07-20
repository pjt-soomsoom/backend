package com.soomsoom.backend.application.port.`in`.auth.command

data class AdminLoginCommand(
    val username: String,
    val password: String,
)
