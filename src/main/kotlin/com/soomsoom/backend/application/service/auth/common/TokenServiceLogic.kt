package com.soomsoom.backend.application.service.auth.common

import com.soomsoom.backend.application.port.out.auth.RefreshTokenPort
import com.soomsoom.backend.application.port.out.auth.TokenResult
import com.soomsoom.backend.domain.auth.RefreshToken
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
class TokenServiceLogic(
    private val refreshTokenPort: RefreshTokenPort,
) {
    @Transactional
    fun manageRefreshToken(userId: Long, tokenResult: TokenResult) {
        // 새로운 리프레시 토큰을 저장
        val newRefreshToken = RefreshToken(
            token = tokenResult.refreshToken,
            userId = userId,
            expiryDate = tokenResult.refreshTokenExpiry
        )
        refreshTokenPort.save(newRefreshToken)
    }
}
