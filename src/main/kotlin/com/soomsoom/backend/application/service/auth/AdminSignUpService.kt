package com.soomsoom.backend.application.service.auth

import com.soomsoom.backend.application.port.`in`.auth.TokenInfo
import com.soomsoom.backend.application.port.`in`.auth.command.AdminSignUpCommand
import com.soomsoom.backend.application.port.`in`.auth.usecase.AdminSignUpUseCase
import com.soomsoom.backend.application.port.out.auth.TokenGeneratorPort
import com.soomsoom.backend.application.port.out.user.UserPort
import com.soomsoom.backend.common.exception.SoomSoomException
import com.soomsoom.backend.domain.user.UserErrorCode
import com.soomsoom.backend.domain.user.model.User
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class AdminSignUpService(
    private val userPort: UserPort,
    private val passwordEncoder: PasswordEncoder,
    private val tokenGeneratorPort: TokenGeneratorPort,
) : AdminSignUpUseCase {
    override fun adminSignUp(command: AdminSignUpCommand): TokenInfo {
        userPort.findByUsername(command.username)?.let {
            throw SoomSoomException(UserErrorCode.USER_ALREADY_EXISTS)
        }

        return passwordEncoder.encode(command.password)
            .let { encodedPassword ->
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
            .let(tokenGeneratorPort::generateToken)
            .let { TokenInfo(it) }
    }
}
