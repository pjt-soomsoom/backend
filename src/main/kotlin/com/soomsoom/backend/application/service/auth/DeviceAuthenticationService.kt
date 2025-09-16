package com.soomsoom.backend.application.service.auth

import com.soomsoom.backend.adapter.`in`.security.service.CustomUserDetails
import com.soomsoom.backend.application.port.`in`.auth.TokenInfo
import com.soomsoom.backend.application.port.`in`.auth.command.DeviceAuthenticationCommand
import com.soomsoom.backend.application.port.`in`.auth.usecase.command.AuthenticateWithDeviceUseCase
import com.soomsoom.backend.application.port.out.auth.TokenGeneratorPort
import com.soomsoom.backend.application.port.out.user.UserPort
import com.soomsoom.backend.application.service.auth.common.TokenServiceLogic
import com.soomsoom.backend.common.event.Event
import com.soomsoom.backend.common.event.EventType
import com.soomsoom.backend.common.event.payload.UserAuthenticatedPayload
import com.soomsoom.backend.domain.user.model.Account
import com.soomsoom.backend.domain.user.model.aggregate.Role
import com.soomsoom.backend.domain.user.model.aggregate.User
import org.springframework.context.ApplicationEventPublisher
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class DeviceAuthenticationService(
    private val userPort: UserPort,
    private val tokenGeneratorPort: TokenGeneratorPort,
    private val tokenServiceLogic: TokenServiceLogic,
    private val eventPublisher: ApplicationEventPublisher,
) : AuthenticateWithDeviceUseCase {
    override fun authenticate(command: DeviceAuthenticationCommand): TokenInfo {
        val user = userPort.findByDeviceId(command.deviceId)
            ?: userPort.save(User.createAnonymous(command.deviceId))

        val sessionRole = Role.ROLE_ANONYMOUS
        val account = user.account
        val deviceId = when (account) {
            is Account.Anonymous -> account.deviceId
            is Account.Social -> account.deviceId
            else -> null
        }

        val principal = CustomUserDetails.of(user = user, deviceId = deviceId, sessionRole = sessionRole)
        val authentication = UsernamePasswordAuthenticationToken(principal, "", principal.authorities)

        val tokenResult = tokenGeneratorPort.generateToken(authentication)
        tokenServiceLogic.manageRefreshToken(user.id!!, tokenResult)

        eventPublisher.publishEvent(
            Event(
                eventType = EventType.USER_AUTHENTICATED,
                payload = UserAuthenticatedPayload(userId = user.id!!)
            )
        )

        return TokenInfo(tokenResult.accessToken, tokenResult.refreshToken)
    }
}
