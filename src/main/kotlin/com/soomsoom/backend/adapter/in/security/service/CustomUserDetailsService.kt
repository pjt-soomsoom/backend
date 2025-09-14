package com.soomsoom.backend.adapter.`in`.security.service

import com.soomsoom.backend.application.port.out.user.UserPort
import com.soomsoom.backend.common.exception.SoomSoomException
import com.soomsoom.backend.domain.user.UserErrorCode
import com.soomsoom.backend.domain.user.model.Account
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
        val user = userPort.findByUsername(username)
            ?: throw SoomSoomException(UserErrorCode.USERNAME_OR_PASSWORD_MISMATCH)

        // user.account가 IdPassword 타입인지 확인하여 ClassCastException 방지
        if (user.account !is Account.IdPassword) {
            // 아이디/비밀번호 계정이 아니므로 인증 처리 불가
            throw SoomSoomException(UserErrorCode.USERNAME_OR_PASSWORD_MISMATCH)
        }

        // 이 시점부터 user는 IdPassword 계정을 가진 것이 보장됨
        return CustomUserDetails.of(user = user, sessionRole = user.role)
    }
}
