package com.soomsoom.backend.common.config

import com.google.auth.oauth2.GoogleCredentials
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import com.google.firebase.messaging.FirebaseMessaging
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.io.ByteArrayInputStream
import java.io.IOException

@Configuration
class FirebaseConfig(
    @Value("\${firebase.service-account}")
    private val serviceAccountJson: String,
) {
    private val log = LoggerFactory.getLogger(javaClass)

    @Bean
    fun firebaseApp(): FirebaseApp {
        val apps = FirebaseApp.getApps()
        if (apps.isNotEmpty()) {
            return apps.first()
        }

        log.info("Initializing Firebase App...")

        // 'classpath:' 접두사를 제거하고 실제 경로만 사용합니다.
        try {
            val options = FirebaseOptions.builder()
                .setCredentials(GoogleCredentials.fromStream(ByteArrayInputStream(serviceAccountJson.toByteArray())))
                .build()
            return FirebaseApp.initializeApp(options)
        } catch (e: IOException) {
            log.error("Error initializing Firebase from service account JSON.", e)
            throw IllegalStateException("Failed to initialize Firebase App", e)
        }
    }

    @Bean
    fun firebaseMessaging(firebaseApp: FirebaseApp): FirebaseMessaging {
        return FirebaseMessaging.getInstance(firebaseApp)
    }
}
