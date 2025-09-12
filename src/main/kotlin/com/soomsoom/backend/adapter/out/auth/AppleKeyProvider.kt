package com.soomsoom.backend.adapter.out.auth

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import org.springframework.stereotype.Component
import org.springframework.web.client.RestTemplate
import java.math.BigInteger
import java.security.KeyFactory
import java.security.PublicKey
import java.security.spec.RSAPublicKeySpec
import java.util.Base64
import java.util.concurrent.ConcurrentHashMap

data class ApplePublicKey(
    val kty: String,
    val kid: String,
    val use: String,
    val alg: String,
    val n: String,
    val e: String,
)

data class ApplePublicKeys(val keys: List<ApplePublicKey>)

@Component
class AppleKeyProvider(
    private val restTemplate: RestTemplate,
    private val objectMapper: ObjectMapper
) {
    private val appleAuthKeysUrl = "https://appleid.apple.com/auth/keys"
    private val keyCache: ConcurrentHashMap<String, PublicKey> = ConcurrentHashMap()

    fun getPublicKey(tokenHeader: Map<String, Any>): PublicKey {
        val keyId = tokenHeader["kid"] as String
        // 캐시된 키가 있으면 바로 반환
        return keyCache[keyId] ?: run {
            // 캐시에 없으면 Apple 서버에서 모든 키를 가져와 캐시를 갱신
            updateKeyCache()
            // 갱신된 캐시에서 다시 조회
            keyCache[keyId] ?: throw IllegalArgumentException("Apple ID Token의 kid와 일치하는 Public Key를 찾을 수 없습니다.")
        }
    }

    private fun updateKeyCache() {
        val response = restTemplate.getForObject(appleAuthKeysUrl, String::class.java)
        val publicKeys: ApplePublicKeys = objectMapper.readValue(response!!)

        publicKeys.keys.forEach { key ->
            val n = BigInteger(1, Base64.getUrlDecoder().decode(key.n))
            val e = BigInteger(1, Base64.getUrlDecoder().decode(key.e))
            val keySpec = RSAPublicKeySpec(n, e)
            val keyFactory = KeyFactory.getInstance(key.kty)
            val publicKey = keyFactory.generatePublic(keySpec)
            keyCache[key.kid] = publicKey
        }
    }
}
