package com.soomsoom.backend.adapter.out.auth

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.json.gson.GsonFactory
import com.soomsoom.backend.application.port.out.auth.SocialProfileInfo
import com.soomsoom.backend.application.port.out.auth.VerifySocialTokenPort
import com.soomsoom.backend.common.exception.SoomSoomException
import com.soomsoom.backend.domain.user.UserErrorCode
import com.soomsoom.backend.domain.user.model.enums.SocialProvider
import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.util.*

@Component
class SocialVerificationAdapter(
    @Value("\${oauth.google.client-id}") private val googleClientId: String,
    @Value("\${oauth.apple.client-id}") private val appleClientId: String,
    private val appleKeyProvider: AppleKeyProvider,
) : VerifySocialTokenPort {

    private val objectMapper = jacksonObjectMapper()

    override fun verify(provider: SocialProvider, token: String): SocialProfileInfo {
        return when (provider) {
            SocialProvider.GOOGLE -> verifyGoogleToken(token)
            SocialProvider.APPLE -> verifyAppleToken(token)
        }
    }

    private fun verifyGoogleToken(idTokenString: String): SocialProfileInfo {
        val verifier = GoogleIdTokenVerifier.Builder(NetHttpTransport(), GsonFactory.getDefaultInstance())
            .setAudience(Collections.singletonList(googleClientId))
            .build()

        val idToken: GoogleIdToken = verifier.verify(idTokenString)
            ?: throw throw SoomSoomException(UserErrorCode.PROVIDER_TOKEN_INVALID)

        val payload: GoogleIdToken.Payload = idToken.payload

        return SocialProfileInfo(
            provider = SocialProvider.GOOGLE,
            socialId = payload.subject,
            email = payload.email
        )
    }

    private fun verifyAppleToken(identityToken: String): SocialProfileInfo {
        try {
            // 토큰을 '.' 기준으로 분리하여 헤더 부분만 추출
            val headerString = identityToken.substringBefore('.')
            val headerData = Base64.getUrlDecoder().decode(headerString)
            val header: Map<String, Any> = objectMapper.readValue(headerData)

            // 헤더에서 kid를 사용하여 Apple의 공개키를 가져옴
            val publicKey = appleKeyProvider.getPublicKey(header)

            // 공개키로 서명 검증 및 최종 클레임 추출
            val claims: Claims = Jwts.parser()
                .verifyWith(publicKey)
                .build()
                .parseSignedClaims(identityToken)
                .payload

            // aud(audience) 클레임 검증
            if (appleClientId !in claims.audience) {
                throw IllegalArgumentException("Apple ID Token의 aud(audience) 값이 일치하지 않습니다.")
            }

            return SocialProfileInfo(
                provider = SocialProvider.APPLE,
                socialId = claims.subject,
                email = claims.get("email", String::class.java)
            )
        } catch (e: Exception) {
            throw IllegalArgumentException("유효하지 않은 Apple Identity Token입니다.", e)
        }
    }
}
