package com.soomsoom.backend.adapter.out.s3.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.s3.S3Client
import software.amazon.awssdk.services.s3.presigner.S3Presigner

@Configuration
class AwsConfig(
    @Value("\${aws.region}")
    private val region: String,
) {

    @Bean
    fun s3PreSigner(): S3Presigner {
        return S3Presigner.builder()
            .region(Region.of(region))
            .build()
    }

    @Bean
    fun s3Client(): S3Client {
        return S3Client.builder()
            .region(Region.of(region))
            .build()
    }
}
