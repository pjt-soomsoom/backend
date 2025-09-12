package com.soomsoom.backend.adapter.`in`.web.api.auth.request

import jakarta.validation.constraints.NotBlank

data class LogoutRequest(
    @field:NotBlank
    val refreshToken: String,
)
