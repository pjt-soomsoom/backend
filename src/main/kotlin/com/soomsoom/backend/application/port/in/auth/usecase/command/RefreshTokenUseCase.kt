package com.soomsoom.backend.application.port.`in`.auth.usecase.command

import com.soomsoom.backend.application.port.`in`.auth.TokenInfo

interface RefreshTokenUseCase {
    fun refreshToken(refreshToken: String): TokenInfo
    fun deleteByUserId(userId: Long)
}
