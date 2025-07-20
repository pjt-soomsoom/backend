package com.soomsoom.backend.application.service.auth

import com.soomsoom.backend.adapter.`in`.security.provider.JwtTokenProvider
import com.soomsoom.backend.application.port.`in`.auth.TokenInfo
import com.soomsoom.backend.application.port.`in`.auth.command.AdminSignUpCommand
import com.soomsoom.backend.application.port.`in`.auth.usecase.AdminSignUpUseCase
import com.soomsoom.backend.application.port.out.user.UserPort
import com.soomsoom.backend.domain.user.model.User
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service

@Service
class AdminSignUpService(
    private val userPort: UserPort,
    private val passwordEncoder: PasswordEncoder,
    private val jwtTokenProvider: JwtTokenProvider,
) : AdminSignUpUseCase {
    /**
     * 관리자 계정을 신규로 등록하고 JWT 토큰을 반환합니다.
     *
     * 주어진 사용자명으로 이미 계정이 존재하면 예외를 발생시킵니다.
     *
     * @param command 관리자 회원가입에 필요한 사용자명과 비밀번호 정보를 담은 명령 객체
     * @return 생성된 관리자 계정에 대한 JWT 토큰 정보
     * @throws IllegalStateException 이미 존재하는 사용자명일 경우 발생
     */
    override fun adminSignUp(command: AdminSignUpCommand): TokenInfo {
        userPort.findByUsername(command.username)?.let {
            throw IllegalStateException("이미 존재하는 계정 이름")
        }

        return passwordEncoder.encode(command.password)
            .let { encodedPassword ->
                println(encodedPassword)
                User.createAdmin(command.username, encodedPassword)
            }
            .let(userPort::save)
            .let { savedUser ->
                val authorities = listOf(SimpleGrantedAuthority(savedUser.role.name))
                val principal = org.springframework.security.core.userdetails.User(
                    savedUser.id.toString(),
                    "",
                    authorities
                )
                UsernamePasswordAuthenticationToken(principal, "", authorities)
            }
            .let(jwtTokenProvider::generateToken)
            .let { TokenInfo(it) }
    }
}
