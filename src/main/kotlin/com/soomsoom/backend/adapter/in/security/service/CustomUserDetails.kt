package com.soomsoom.backend.adapter.`in`.security.service

import com.soomsoom.backend.domain.user.model.Account
import com.soomsoom.backend.domain.user.model.aggregate.Role
import com.soomsoom.backend.domain.user.model.aggregate.User
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails

class CustomUserDetails(
    val id: Long,
    private val username: String,
    private val password: String,
    val deviceId: String? = null,
    private val authorities: MutableCollection<out GrantedAuthority>,
) : UserDetails {

    override fun getAuthorities(): MutableCollection<out GrantedAuthority> = authorities
    override fun getPassword(): String = password
    override fun getUsername(): String = username

    override fun isAccountNonExpired(): Boolean = true
    override fun isAccountNonLocked(): Boolean = true
    override fun isCredentialsNonExpired(): Boolean = true
    override fun isEnabled(): Boolean = true

    companion object {
        fun of(user: User, deviceId: String? = null, sessionRole: Role): CustomUserDetails {
            val username = when (val acc = user.account) {
                is Account.IdPassword -> acc.username
                else -> user.id!!.toString()
            }
            val password = (user.account as? Account.IdPassword)?.password ?: ""

            return CustomUserDetails(
                id = user.id!!,
                username = username,
                password = password,
                deviceId = deviceId,
                authorities = mutableListOf(SimpleGrantedAuthority(sessionRole.name))
            )
        }
    }
}
