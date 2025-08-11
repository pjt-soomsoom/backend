package com.soomsoom.backend.fixture

import org.springframework.security.core.authority.SimpleGrantedAuthority

object TestUserFixture {
    val ADMIN = MockUserDetails(
        userId = 1L,
        username = "admin@soomsoom.io",
        authorities = listOf(SimpleGrantedAuthority("ROLE_ADMIN"))
    )

    val USER = MockUserDetails(
        userId = 2L,
        username = "user@soomsoom.io",
        authorities = listOf(SimpleGrantedAuthority("ROLE_USER"))
    )
}
