package com.soomsoom.backend.application.service.auth

import com.soomsoom.backend.application.port.`in`.auth.TokenInfo
import com.soomsoom.backend.application.port.`in`.auth.command.AdminLoginCommand
import com.soomsoom.backend.application.port.`in`.auth.usecase.AdminLoginUseCase
import com.soomsoom.backend.application.port.out.auth.TokenGeneratorPort
import com.soomsoom.backend.application.port.out.user.UserPort
import com.soomsoom.backend.common.exception.SoomSoomException
import com.soomsoom.backend.domain.user.UserErrorCode
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.core.userdetails.User
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class AdminLoginService(
    private val userPort: UserPort,
    private val tokenGeneratorPort: TokenGeneratorPort,
    private val authenticationManagerBuilder: AuthenticationManagerBuilder,
) : AdminLoginUseCase {
    override fun adminLogin(command: AdminLoginCommand): TokenInfo {
        val authenticationToken = UsernamePasswordAuthenticationToken(command.username, command.password)
        val authResult = authenticationManagerBuilder.`object`.authenticate(authenticationToken)

        return userPort.findByUsername(authResult.name)
            ?.let { user ->
                UsernamePasswordAuthenticationToken(
                    User(user.id.toString(), "", authResult.authorities),
                    "",
                    authResult.authorities
                )
            }
            ?.let(tokenGeneratorPort::generateToken)
            ?.let { TokenInfo(it) }
            ?: throw SoomSoomException(UserErrorCode.USER_USERNAME_OR_PASSWORD_MISMATCH)
    }
}
