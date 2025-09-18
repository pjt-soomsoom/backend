package com.soomsoom.backend.application.service.auth

import com.soomsoom.backend.adapter.`in`.security.service.CustomUserDetails
import com.soomsoom.backend.application.port.`in`.auth.TokenInfo
import com.soomsoom.backend.application.port.`in`.auth.command.AdminSignUpCommand
import com.soomsoom.backend.application.port.`in`.auth.usecase.command.AdminSignUpUseCase
import com.soomsoom.backend.application.port.out.auth.TokenGeneratorPort
import com.soomsoom.backend.application.port.out.user.UserPort
import com.soomsoom.backend.application.service.auth.common.TokenServiceLogic
import com.soomsoom.backend.common.event.Event
import com.soomsoom.backend.common.event.EventType
import com.soomsoom.backend.common.event.payload.UserCreatedPayload
import com.soomsoom.backend.common.exception.SoomSoomException
import com.soomsoom.backend.domain.user.UserErrorCode
import com.soomsoom.backend.domain.user.model.aggregate.User
import org.springframework.context.ApplicationEventPublisher
import org.springframework.security.authentication.AuthenticationEventPublisher
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class AdminSignUpService(
    private val userPort: UserPort,
    private val passwordEncoder: PasswordEncoder,
    private val tokenGeneratorPort: TokenGeneratorPort,
    private val tokenServiceLogic: TokenServiceLogic,
    private val eventPublisher: ApplicationEventPublisher,
) : AdminSignUpUseCase {
    override fun adminSignUp(command: AdminSignUpCommand): TokenInfo {
        userPort.findByUsername(command.username)?.let {
            throw SoomSoomException(UserErrorCode.ALREADY_EXISTS)
        }

        val savedUser = passwordEncoder.encode(command.password)
            .let { User.createAdmin(command.username, it) }
            .let { userPort.save(it) }

        eventPublisher.publishEvent(
            Event(
                eventType = EventType.USER_CREATED,
                payload = UserCreatedPayload(userId = savedUser.id!!)
            )
        )
        val sessionRole = savedUser.role
        val principal = CustomUserDetails.of(user = savedUser, sessionRole = sessionRole)
        val authentication = UsernamePasswordAuthenticationToken(principal, "", principal.authorities)

        val tokenResult = tokenGeneratorPort.generateToken(authentication)
        tokenServiceLogic.manageRefreshToken(savedUser.id!!, tokenResult)

        return TokenInfo(tokenResult.accessToken, tokenResult.refreshToken)
    }
}
