package com.soomsoom.backend.application.service.auth

import com.soomsoom.backend.adapter.`in`.security.service.CustomUserDetails
import com.soomsoom.backend.application.port.`in`.auth.TokenInfo
import com.soomsoom.backend.application.port.`in`.auth.usecase.command.RefreshTokenUseCase
import com.soomsoom.backend.application.port.out.auth.RefreshTokenPort
import com.soomsoom.backend.application.port.out.auth.TokenGeneratorPort
import com.soomsoom.backend.application.port.out.user.UserPort
import com.soomsoom.backend.application.service.auth.common.TokenServiceLogic
import com.soomsoom.backend.common.exception.SoomSoomException
import com.soomsoom.backend.domain.user.UserErrorCode
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.Instant

@Service
@Transactional
class RefreshTokenService(
    private val refreshTokenPort: RefreshTokenPort,
    private val userPort: UserPort,
    private val tokenGeneratorPort: TokenGeneratorPort,
    private val tokenServiceLogic: TokenServiceLogic
) : RefreshTokenUseCase{
    override fun refreshToken(refreshToken: String): TokenInfo {
        val oldRefreshToken = refreshTokenPort.findByToken(refreshToken)
            ?: throw SoomSoomException(UserErrorCode.REFRESH_TOKEN_NOT_FOUND)

        if (oldRefreshToken.expiryDate.isBefore(Instant.now())) {
            refreshTokenPort.delete(oldRefreshToken)
            throw SoomSoomException(UserErrorCode.REFRESH_TOKEN_EXPIRED)
        }

        val user = userPort.findById(oldRefreshToken.userId)
            ?: throw SoomSoomException(UserErrorCode.NOT_FOUND)

        val sessionRole = user.role
        val principal = CustomUserDetails.of(user, sessionRole)
        val authentication = UsernamePasswordAuthenticationToken(principal, "", principal.authorities)

        val newTokenResult = tokenGeneratorPort.generateToken(authentication)

        // Refresh Token Rotation
        refreshTokenPort.delete(oldRefreshToken)
        tokenServiceLogic.manageRefreshToken(user.id!!, newTokenResult)

        return TokenInfo(newTokenResult.accessToken, newTokenResult.refreshToken)
    }

}
