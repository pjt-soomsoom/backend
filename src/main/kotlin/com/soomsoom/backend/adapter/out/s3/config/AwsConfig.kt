package com.soomsoom.backend.adapter.out.s3.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.s3.presigner.S3Presigner

@Configuration
class AwsConfig(
    @Value("\${cloud.aws.region.static}")
    private val region: String,

    @Value("\${cloud.aws.credentials.access-key}")
    private val accessKey: String,

    @Value("\${cloud.aws.credentials.secret-key}")
    private val secretKey: String,
) {

    @Bean
    fun s3PreSigner(): S3Presigner {
        val credentials = AwsBasicCredentials.create(accessKey, secretKey)
        val credentialsProvider = StaticCredentialsProvider.create(credentials)

        return S3Presigner.builder()
            .region(Region.of(region))
            .credentialsProvider(credentialsProvider)
            .build()
    }
}
