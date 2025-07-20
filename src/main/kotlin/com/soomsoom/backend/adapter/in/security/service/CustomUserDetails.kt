package com.soomsoom.backend.adapter.`in`.security.service

import com.soomsoom.backend.domain.user.model.Account
import com.soomsoom.backend.domain.user.model.User
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails

class CustomUserDetails(
    private val user: User
) : UserDetails {
    override fun getAuthorities(): MutableCollection<out GrantedAuthority> {
        return mutableListOf(SimpleGrantedAuthority(user.role.name))
    }

    override fun getPassword(): String {
        return (user.account as Account.IdPassword).password
    }

    override fun getUsername(): String {
        return (user.account as Account.IdPassword).username
    }

}
