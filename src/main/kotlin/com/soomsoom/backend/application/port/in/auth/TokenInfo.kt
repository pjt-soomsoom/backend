package com.soomsoom.backend.application.port.`in`.auth

data class TokenInfo(
    val accessToken: String,
    val refreshToken: String,
)
