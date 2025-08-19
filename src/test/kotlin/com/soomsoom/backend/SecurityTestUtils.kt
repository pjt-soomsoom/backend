package com.soomsoom.backend

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder

suspend fun <T> withTestUser(role: String, block: suspend () -> T): T {
    // 1. 주어진 role로 가짜 인증 객체를 생성합니다.
    val authorities = listOf(SimpleGrantedAuthority("ROLE_$role"))
    val authentication = UsernamePasswordAuthenticationToken("testuser", "password", authorities)

    // 2. SecurityContextHolder에 인증 객체를 설정하여 '로그인'시킵니다.
    SecurityContextHolder.getContext().authentication = authentication

    try {
        // 3. 전달받은 코드 블록을 실행합니다.
        return block()
    } finally {
        // 4. 테스트가 끝나면 SecurityContext를 깨끗하게 비워 다음 테스트에 영향을 주지 않도록 합니다.
        SecurityContextHolder.clearContext()
    }
}
