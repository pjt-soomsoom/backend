package com.soomsoom.backend.adapter.`in`.web.api.auth.request

import jakarta.validation.constraints.NotBlank

data class RefreshTokenRequest(
    @field:NotBlank
    val refreshToken: String,
)
