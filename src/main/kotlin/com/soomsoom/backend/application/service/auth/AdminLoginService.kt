package com.soomsoom.backend.application.service.auth

import com.soomsoom.backend.adapter.`in`.security.service.CustomUserDetails
import com.soomsoom.backend.application.port.`in`.auth.TokenInfo
import com.soomsoom.backend.application.port.`in`.auth.command.AdminLoginCommand
import com.soomsoom.backend.application.port.`in`.auth.usecase.command.AdminLoginUseCase
import com.soomsoom.backend.application.port.out.auth.TokenGeneratorPort
import com.soomsoom.backend.application.service.auth.common.TokenServiceLogic
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class AdminLoginService(
    private val tokenGeneratorPort: TokenGeneratorPort,
    private val authenticationManagerBuilder: AuthenticationManagerBuilder,
    private val tokenServiceLogic: TokenServiceLogic,
) : AdminLoginUseCase {
    override fun adminLogin(command: AdminLoginCommand): TokenInfo {
        val authenticationToken = UsernamePasswordAuthenticationToken(command.username, command.password)
        val authentication = authenticationManagerBuilder.`object`.authenticate(authenticationToken)

        val tokenResult = tokenGeneratorPort.generateToken(authentication)
        val userId = (authentication.principal as CustomUserDetails).id
        tokenServiceLogic.manageRefreshToken(userId, tokenResult)

        return TokenInfo(tokenResult.accessToken, tokenResult.refreshToken)
    }
}
