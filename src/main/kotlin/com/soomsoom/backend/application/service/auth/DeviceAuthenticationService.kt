package com.soomsoom.backend.application.service.auth

import com.soomsoom.backend.adapter.`in`.security.service.CustomUserDetails
import com.soomsoom.backend.application.port.`in`.auth.TokenInfo
import com.soomsoom.backend.application.port.`in`.auth.command.DeviceAuthenticationCommand
import com.soomsoom.backend.application.port.`in`.auth.usecase.command.AuthenticateWithDeviceUseCase
import com.soomsoom.backend.application.port.out.auth.TokenGeneratorPort
import com.soomsoom.backend.application.port.out.user.UserPort
import com.soomsoom.backend.application.service.auth.common.TokenServiceLogic
import com.soomsoom.backend.domain.user.model.aggregate.Role
import com.soomsoom.backend.domain.user.model.aggregate.User
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class DeviceAuthenticationService(
    private val userPort: UserPort,
    private val tokenGeneratorPort: TokenGeneratorPort,
    private val tokenServiceLogic: TokenServiceLogic,
) : AuthenticateWithDeviceUseCase {
    override fun authenticate(command: DeviceAuthenticationCommand): TokenInfo {
        val user = userPort.findByDeviceId(command.deviceId)
            ?: userPort.save(User.createAnonymous(command.deviceId))

        val sessionRole = Role.ROLE_ANONYMOUS
        val principal = CustomUserDetails.of(user, sessionRole)
        val authentication = UsernamePasswordAuthenticationToken(principal, "", principal.authorities)

        val tokenResult = tokenGeneratorPort.generateToken(authentication)
        tokenServiceLogic.manageRefreshToken(user.id!!, tokenResult)

        return TokenInfo(tokenResult.accessToken, tokenResult.refreshToken)
    }
}
