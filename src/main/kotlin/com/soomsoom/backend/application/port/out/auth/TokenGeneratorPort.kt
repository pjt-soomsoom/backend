package com.soomsoom.backend.application.port.out.auth

import org.springframework.security.core.Authentication

interface TokenGeneratorPort {
    fun generateToken(authentication: Authentication): String
}
