package com.soomsoom.backend.adapter.out.s3

import com.soomsoom.backend.domain.user.FileCategory
import com.soomsoom.backend.domain.user.FileDomain
import com.soomsoom.backend.application.port.out.upload.FileUploadUrlGeneratorPort
import com.soomsoom.backend.application.port.out.upload.dto.FileUploadUrl
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import software.amazon.awssdk.services.s3.model.PutObjectRequest
import software.amazon.awssdk.services.s3.presigner.S3Presigner
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest
import java.time.Duration
import java.util.UUID

@Component
class S3FileUploadUrlGeneratorAdapter(
    private val s3Presigner: S3Presigner,

    @Value("\${cloud.aws.s3.bucket}")
    private val bucketName: String,
) : FileUploadUrlGeneratorPort {

    companion object {
        private const val FILE_PATH_FORMAT = "%s/%d/%s/%s.%s"
        private const val SIGNATURE_DURATION = 10L
    }

    /**
     * presignedUrl 반환 메서드
     */

    override fun generate(
        filename: String,
        domain: FileDomain,
        domainId: Long,
        category: FileCategory,
        contentType: String,
    ): FileUploadUrl {
        val fileKey = createFileKey(filename, domain, domainId, category)

        val putObjectRequest = PutObjectRequest.builder()
            .bucket(bucketName)
            .key(fileKey)
            .contentType(contentType)
            .build()

        val preSignRequest = PutObjectPresignRequest.builder()
            .signatureDuration(Duration.ofMinutes(SIGNATURE_DURATION))
            .putObjectRequest(putObjectRequest)
            .build()

        val preSignedUrl = s3Presigner.presignPutObject(preSignRequest).url().toString()

        return FileUploadUrl(preSignedUrl, fileKey)
    }

    /**
     * bucket에 저장될 파일 경로
     */

    private fun createFileKey(filename: String, domain: FileDomain, domainId: Long, category: FileCategory): String {
        val extension = filename.substringAfterLast(".", "")
        val uuid = UUID.randomUUID().toString()

        val domainPath = domain.name.lowercase()
        val categoryPath = category.name.lowercase()

        return String.format(FILE_PATH_FORMAT, domainPath, domainId, categoryPath, uuid, extension)
    }
}
