package com.soomsoom.backend.adapter.`in`.security.service

import com.soomsoom.backend.domain.user.model.Account
import com.soomsoom.backend.domain.user.model.aggregate.User
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails

class CustomUserDetails(
    val id: Long,
    private val username: String,
    private val password: String,
    private val authorities: MutableCollection<out GrantedAuthority>,
) : UserDetails {

    // 로그인 시 사용될 생성자
    constructor(user: User) : this(
        id = user.id!!,
        username = (user.account as Account.IdPassword).username,
        password = (user.account as Account.IdPassword).password,
        authorities = mutableListOf(SimpleGrantedAuthority(user.role.name))
    )

    override fun getAuthorities(): MutableCollection<out GrantedAuthority> = authorities
    override fun getPassword(): String = password
    override fun getUsername(): String = username

    override fun isAccountNonExpired(): Boolean = true
    override fun isAccountNonLocked(): Boolean = true
    override fun isCredentialsNonExpired(): Boolean = true
    override fun isEnabled(): Boolean = true
}
