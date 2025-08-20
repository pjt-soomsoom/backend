package com.soomsoom.backend.application.service.auth

import com.soomsoom.backend.application.port.`in`.auth.TokenInfo
import com.soomsoom.backend.application.port.`in`.auth.command.AdminLoginCommand
import com.soomsoom.backend.application.port.`in`.auth.usecase.AdminLoginUseCase
import com.soomsoom.backend.application.port.out.auth.TokenGeneratorPort
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class AdminLoginService(
    private val tokenGeneratorPort: TokenGeneratorPort,
    private val authenticationManagerBuilder: AuthenticationManagerBuilder,
) : AdminLoginUseCase {
    override fun adminLogin(command: AdminLoginCommand): TokenInfo {
        val authenticationToken = UsernamePasswordAuthenticationToken(command.username, command.password)
        val authResult = authenticationManagerBuilder.`object`.authenticate(authenticationToken)

        val accessToken = tokenGeneratorPort.generateToken(authResult)
        return TokenInfo(accessToken)
    }
}
