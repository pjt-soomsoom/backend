package com.soomsoom.backend.application.service.auth

import com.soomsoom.backend.application.port.`in`.auth.usecase.command.LogoutUseCase
import com.soomsoom.backend.application.port.out.auth.RefreshTokenPort
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class LogoutService(
    private val refreshTokenPort: RefreshTokenPort
) : LogoutUseCase{
    override fun logout(refreshToken: String) {
        refreshTokenPort.findByToken(refreshToken)?.let {
            refreshTokenPort.delete(it)
        }
    }
}
