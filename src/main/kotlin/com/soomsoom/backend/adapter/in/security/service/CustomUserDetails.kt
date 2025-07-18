package com.soomsoom.backend.adapter.`in`.security.service

import com.soomsoom.backend.application.port.out.user.UserPort
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class CustomUserDetails(
    private val userPort: UserPort,
) : UserDetailsService {

    @Transactional(readOnly = true)
    override fun loadUserByUsername(username: String): UserDetails {
        val user = (
            userPort.findById(username.toLong())
                ?: throw UsernameNotFoundException("해당 유저를 찾을 수 없습니다: $username")
            )

        return User.builder()
            .username(user.id.toString())
            .roles(user.role.name)
            .build()
    }
}
