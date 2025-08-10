package com.soomsoom.backend.adapter.out.s3

import com.soomsoom.backend.application.port.out.upload.FileValidatorPort
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import software.amazon.awssdk.services.s3.S3Client
import software.amazon.awssdk.services.s3.model.HeadObjectRequest
import software.amazon.awssdk.services.s3.model.NoSuchKeyException

@Component
class S3FileValidatorAdapter(
    private val s3Client: S3Client,
    @Value("\${cloud.aws.s3.bucket}")
    private val bucket: String,
) : FileValidatorPort {
    override fun validate(fileKey: String): Boolean {
        return try {
            val headObjectRequest = HeadObjectRequest.builder()
                .bucket(bucket)
                .key(fileKey)
                .build()
            s3Client.headObject(headObjectRequest)
            true
        } catch (e: NoSuchKeyException) {
            false
        }
    }
}
