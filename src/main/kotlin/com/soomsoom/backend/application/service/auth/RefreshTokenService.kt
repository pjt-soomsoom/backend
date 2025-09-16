package com.soomsoom.backend.application.service.auth

import com.soomsoom.backend.adapter.`in`.security.service.CustomUserDetails
import com.soomsoom.backend.application.port.`in`.auth.TokenInfo
import com.soomsoom.backend.application.port.`in`.auth.usecase.command.RefreshTokenUseCase
import com.soomsoom.backend.application.port.out.auth.RefreshTokenPort
import com.soomsoom.backend.application.port.out.auth.TokenGeneratorPort
import com.soomsoom.backend.application.port.out.user.UserPort
import com.soomsoom.backend.application.service.auth.common.TokenServiceLogic
import com.soomsoom.backend.common.event.Event
import com.soomsoom.backend.common.event.EventType
import com.soomsoom.backend.common.event.payload.UserAuthenticatedPayload
import com.soomsoom.backend.common.exception.SoomSoomException
import com.soomsoom.backend.domain.user.UserErrorCode
import com.soomsoom.backend.domain.user.model.Account
import org.springframework.context.ApplicationEventPublisher
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
    private val tokenServiceLogic: TokenServiceLogic,
    private val eventPublisher: ApplicationEventPublisher,
) : RefreshTokenUseCase {
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
        val account = user.account
        val deviceId = when (account) {
            is Account.Anonymous -> account.deviceId
            is Account.Social -> account.deviceId
            else -> null
        }
        val principal = CustomUserDetails.of(user, deviceId, sessionRole)
        val authentication = UsernamePasswordAuthenticationToken(principal, "", principal.authorities)

        val newTokenResult = tokenGeneratorPort.generateToken(authentication)

        // Refresh Token Rotation
        refreshTokenPort.delete(oldRefreshToken)
        tokenServiceLogic.manageRefreshToken(user.id!!, newTokenResult)

        eventPublisher.publishEvent(
            Event(
                eventType = EventType.USER_AUTHENTICATED,
                payload = UserAuthenticatedPayload(userId = user.id!!)
            )
        )

        return TokenInfo(newTokenResult.accessToken, newTokenResult.refreshToken)
    }
}
