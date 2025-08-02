package com.soomsoom.backend.adapter.`in`.security.service

import com.soomsoom.backend.application.port.out.user.UserPort
import com.soomsoom.backend.common.exception.SoomSoomException
import com.soomsoom.backend.domain.user.UserErrorCode
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class CustomUserDetailsService(
    private val userPort: UserPort,
) : UserDetailsService {

    @Transactional(readOnly = true)
    override fun loadUserByUsername(username: String): UserDetails {
        val user = (
            userPort.findByUsername(username)
                ?: throw SoomSoomException(UserErrorCode.USERNAME_OR_PASSWORD_MISMATCH)
            )

        return CustomUserDetails(user)
    }
}
