package com.soomsoom.backend.adapter.out.crypto

import com.google.crypto.tink.apps.rewardedads.RewardedAdsVerifier
import com.soomsoom.backend.application.port.out.adrewardlog.AdMobVerificationPort
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import java.security.GeneralSecurityException

@Component
class AdMobVerificationAdapter : AdMobVerificationPort {
    private val logger = LoggerFactory.getLogger(this::class.java)

    override fun verify(fullCallbackUrl: String): Boolean {
        return try {
            val verifier = RewardedAdsVerifier.Builder()
                .build()
            verifier.verify(fullCallbackUrl)
            true
        } catch (e: GeneralSecurityException) {
            logger.error("AdMob SSV verification failed: ${e.message}", e)
            false
        }
    }
}
