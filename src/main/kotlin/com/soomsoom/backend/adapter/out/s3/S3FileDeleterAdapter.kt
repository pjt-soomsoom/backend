package com.soomsoom.backend.adapter.out.s3

import com.soomsoom.backend.application.port.out.upload.FileDeleterPort
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import software.amazon.awssdk.services.s3.S3Client
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest

@Component
class S3FileDeleterAdapter(
    private val s3Client: S3Client,
    @Value("\${cloud.aws.s3.bucket}")
    private val bucket: String,
) : FileDeleterPort {

    override fun delete(fileKey: String) {
        val deleteObjectRequest = DeleteObjectRequest.builder()
            .bucket(bucket)
            .key(fileKey)
            .build()

        s3Client.deleteObject(deleteObjectRequest)
    }
}
