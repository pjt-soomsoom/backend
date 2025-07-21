package com.soomsoom.backend.adapter.`in`.security.provider

import com.soomsoom.backend.application.port.out.auth.TokenGeneratorPort
import io.jsonwebtoken.Claims
import io.jsonwebtoken.ExpiredJwtException
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.io.Decoders
import io.jsonwebtoken.security.Keys
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.User
import org.springframework.stereotype.Component
import java.util.*
import javax.crypto.SecretKey

@Component
class JwtTokenProvider(
    @Value("\${jwt.secret}") private val secretKey: String,
    @Value("\${jwt.access.expiration}") private val accessTokenExpiration: Long,
) : TokenGeneratorPort {
    private val key: SecretKey

    init {
        val keyBytes = Decoders.BASE64.decode(secretKey)
        key = Keys.hmacShaKeyFor(keyBytes)
    }

    /**
     * 인증 객체로 액세스 토큰 생성
     */

    override fun generateToken(authentication: Authentication): String {
        val authorities = authentication.authorities.joinToString(",") { it.authority }
        val now = Date()
        val validity = Date(now.time + accessTokenExpiration)

        return Jwts.builder()
            .subject(authentication.name)
            .claim("auth", authorities)
            .issuedAt(now)
            .expiration(validity)
            .signWith(key)
            .compact()
    }

    /**
     * Token 유효성 검증
     */

    fun validateToken(token: String): Boolean {
        return runCatching {
            Jwts.parser().verifyWith(key).build().parseSignedClaims(token)
            true
        }.getOrElse {
            false
        }
    }

    /**
     * JWT 토큰에서 인증 객체 추출
     */

    fun getAuthentication(token: String): Authentication {
        val claims = parseClaims(token)

        val authorities = (
            claims["auth"]?.toString()?.split(",")
                ?.map { SimpleGrantedAuthority(it) }
                ?: emptyList<GrantedAuthority>()
            )

        val principal = User(claims.subject, "", authorities)
        return UsernamePasswordAuthenticationToken(principal, "", authorities)
    }

    /**
     * JWT 토큰의 페이로드 파싱
     */

    private fun parseClaims(token: String): Claims {
        return try {
            Jwts.parser().verifyWith(key).build().parseSignedClaims(token).payload
        } catch (e: ExpiredJwtException) {
            e.claims
        }
    }
}
