package com.soomsoom.backend.application.port.out.auth

import com.soomsoom.backend.domain.auth.RefreshToken

interface RefreshTokenPort {
    fun findByToken(token: String): RefreshToken?
    fun save(refreshToken: RefreshToken): RefreshToken
    fun delete(refreshToken: RefreshToken)
    fun findByUserId(userId: Long): List<RefreshToken>
    fun deleteAllByUserId(userId: Long)
}
