package com.soomsoom.backend.adapter.`in`.security.service

import com.soomsoom.backend.domain.user.model.Account
import com.soomsoom.backend.domain.user.model.User
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails

class CustomUserDetails(
    private val user: User,
) : UserDetails {
    /**
     * 사용자의 역할에 해당하는 권한 정보를 반환합니다.
     *
     * @return 사용자의 역할 이름을 기반으로 생성된 단일 권한의 컬렉션
     */
    override fun getAuthorities(): MutableCollection<out GrantedAuthority> {
        return mutableListOf(SimpleGrantedAuthority(user.role.name))
    }

    /**
     * 사용자의 비밀번호를 반환합니다.
     *
     * 반환되는 비밀번호는 사용자의 계정이 `Account.IdPassword` 타입일 때 해당 비밀번호입니다.
     * 
     * @return 사용자의 비밀번호 문자열
     */
    override fun getPassword(): String {
        return (user.account as Account.IdPassword).password
    }

    /**
     * 사용자의 계정에서 사용자명을 반환합니다.
     *
     * 사용자의 계정이 항상 `Account.IdPassword` 타입이라고 가정합니다.
     * @return 사용자명 문자열
     */
    override fun getUsername(): String {
        return (user.account as Account.IdPassword).username
    }
}
