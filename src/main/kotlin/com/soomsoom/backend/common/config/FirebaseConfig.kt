package com.soomsoom.backend.common.config

import com.google.auth.oauth2.GoogleCredentials
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import com.google.firebase.messaging.FirebaseMessaging
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.io.ClassPathResource
import java.io.IOException

@Configuration
class FirebaseConfig(
    @Value("\${firebase.service-account-key-path}")
    private val serviceAccountKeyPath: String,
) {
    private val log = LoggerFactory.getLogger(javaClass)

    @Bean
    fun firebaseApp(): FirebaseApp {
        val apps = FirebaseApp.getApps()
        if (apps.isNotEmpty()) {
            return apps.first()
        }

        // 'classpath:' 접두사를 제거하고 실제 경로만 사용합니다.
        val resource = ClassPathResource(serviceAccountKeyPath.removePrefix("classpath:"))

        if (!resource.exists()) {
            log.error("Firebase 서비스 계정 키 파일이 경로에 존재하지 않습니다: {}", serviceAccountKeyPath)
            throw IOException("Service account key file not found at path: $serviceAccountKeyPath")
        }

        try {
            val options = FirebaseOptions.builder()
                .setCredentials(GoogleCredentials.fromStream(resource.inputStream))
                .build()
            return FirebaseApp.initializeApp(options)
        } catch (e: IOException) {
            log.error("Firebase 서비스 계정 키 파일을 읽는 중 오류가 발생했습니다.", e)
            throw e
        }
    }

    @Bean
    fun firebaseMessaging(firebaseApp: FirebaseApp): FirebaseMessaging {
        return FirebaseMessaging.getInstance(firebaseApp)
    }
}
