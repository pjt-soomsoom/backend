package com.soomsoom.backend.adapter.`in`.security.provider

import com.soomsoom.backend.adapter.`in`.security.service.CustomUserDetails
import com.soomsoom.backend.application.port.out.auth.TokenGeneratorPort
import com.soomsoom.backend.application.port.out.auth.TokenResult
import io.jsonwebtoken.Claims
import io.jsonwebtoken.ExpiredJwtException
import io.jsonwebtoken.JwtParser
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.io.Decoders
import io.jsonwebtoken.security.Keys
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.stereotype.Component
import java.util.*
import javax.crypto.SecretKey

@Component
class JwtTokenProvider(
    @Value("\${jwt.secret}") private val secretKey: String,
    @Value("\${jwt.access.expiration}") private val accessTokenExpiration: Long,
    @Value("\${jwt.refresh.expiration}") private val refreshTokenExpiration: Long,
) : TokenGeneratorPort {
    private val key: SecretKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secretKey))
    private val parser: JwtParser = Jwts.parser().verifyWith(key).build()

    /**
     * 인증 객체로 액세스 토큰 생성
     */

    override fun generateToken(authentication: Authentication): TokenResult {
        val accessToken = createAccessToken(authentication)
        val refreshToken = createRefreshToken(authentication)
        val refreshTokenExpiry = parser.parseSignedClaims(refreshToken).payload.expiration.toInstant()

        return TokenResult(accessToken, refreshToken, refreshTokenExpiry)
    }

    /**
     * Token 유효성 검증
     */

    fun validateToken(token: String) {
        parser.parseSignedClaims(token)
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

        val principal = CustomUserDetails(
            id = (claims["userId"] as Number).toLong(),
            username = claims.subject,
            password = "",
            deviceId = claims["deviceId"]?.toString(),
            authorities = authorities.toMutableList()
        )
        return UsernamePasswordAuthenticationToken(principal, "", authorities)
    }

    /**
     * JWT 토큰의 페이로드 파싱
     */

    private fun parseClaims(token: String): Claims {
        return try {
            parser.parseSignedClaims(token).payload
        } catch (e: ExpiredJwtException) {
            e.claims
        }
    }

    /**
     * accessToken 생성
     */
    private fun createAccessToken(authentication: Authentication): String {
        val authorities = authentication.authorities.joinToString(",") { it.authority }
        val now = Date()
        val validity = Date(now.time + accessTokenExpiration)

        val userId = (authentication.principal as? CustomUserDetails)?.id
            ?: throw IllegalStateException("Authentication principal is not CustomUserDetails")

        val deviceId = (authentication.principal as? CustomUserDetails)?.deviceId

        return Jwts.builder()
            .subject(authentication.name)
            .claim("auth", authorities)
            .claim("userId", userId)
            .claim("deviceId", deviceId)
            .issuedAt(now)
            .expiration(validity)
            .signWith(key)
            .compact()
    }

    /**
     * refreshToken 생성
     */
    private fun createRefreshToken(authentication: Authentication): String {
        val now = Date()
        val validity = Date(now.time + refreshTokenExpiration)

        val userDetails = authentication.principal as? CustomUserDetails
            ?: throw IllegalStateException("Authentication principal is not CustomUserDetails")
        val userId = userDetails.id

        return Jwts.builder()
            .subject(userId.toString())
            .issuedAt(now)
            .expiration(validity)
            .signWith(key)
            .compact()
    }
}
