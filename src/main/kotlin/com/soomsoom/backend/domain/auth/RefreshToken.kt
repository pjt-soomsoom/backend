package com.soomsoom.backend.domain.auth

import java.time.Instant

data class RefreshToken(
    val token: String,
    val userId: Long,
    val expiryDate: Instant
)
