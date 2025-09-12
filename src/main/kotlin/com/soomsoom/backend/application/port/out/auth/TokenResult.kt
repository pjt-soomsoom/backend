package com.soomsoom.backend.application.port.out.auth

import java.time.Instant

data class TokenResult(
    val accessToken: String,
    val refreshToken: String,
    val refreshTokenExpiry: Instant,
)
