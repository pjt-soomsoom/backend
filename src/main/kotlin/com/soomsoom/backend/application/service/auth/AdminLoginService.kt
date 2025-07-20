package com.soomsoom.backend.application.service.auth

import com.soomsoom.backend.adapter.`in`.security.provider.JwtTokenProvider
import com.soomsoom.backend.application.port.`in`.auth.TokenInfo
import com.soomsoom.backend.application.port.`in`.auth.command.AdminLoginCommand
import com.soomsoom.backend.application.port.`in`.auth.usecase.AdminLoginUseCase
import com.soomsoom.backend.application.port.out.user.UserPort
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class AdminLoginService(
    private val userPort: UserPort,
    private val jwtTokenProvider: JwtTokenProvider,
    private val authenticationManagerBuilder: AuthenticationManagerBuilder,
) : AdminLoginUseCase {
    /**
     * 관리자 로그인을 처리하고 JWT 토큰을 반환합니다.
     *
     * @param command 로그인에 필요한 관리자 계정 정보가 포함된 명령 객체입니다.
     * @return 인증에 성공하면 JWT 토큰 정보를 반환합니다.
     * @throws UsernameNotFoundException 해당 사용자명을 가진 사용자가 존재하지 않을 경우 발생합니다.
     */
    override fun adminLogin(command: AdminLoginCommand): TokenInfo {
        val authenticationToken = UsernamePasswordAuthenticationToken(command.username, command.password)
        println(command.password)
        val authResult = authenticationManagerBuilder.`object`.authenticate(authenticationToken)
        println(123123123123123)

        return userPort.findByUsername(authResult.name)
            ?.let { user ->
                UsernamePasswordAuthenticationToken(
                    User(user.id.toString(), "", authResult.authorities),
                    "",
                    authResult.authorities
                )
            }
            ?.let(jwtTokenProvider::generateToken)
            ?.let { TokenInfo(it) }
            ?: throw UsernameNotFoundException("User not found with username: ${authResult.name}")
    }
}
