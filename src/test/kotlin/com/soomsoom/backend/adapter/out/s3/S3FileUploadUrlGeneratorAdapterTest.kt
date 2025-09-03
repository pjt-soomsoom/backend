package com.soomsoom.backend.adapter.out.s3

import com.soomsoom.backend.domain.upload.type.FileCategory
import com.soomsoom.backend.domain.upload.type.FileDomain
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import io.kotest.matchers.string.shouldEndWith
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import software.amazon.awssdk.services.s3.presigner.S3Presigner
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest
import java.net.URL

class S3FileUploadUrlGeneratorAdapterTest : BehaviorSpec({
    val s3Presigner = mockk<S3Presigner>()
    val bucketName = "test-bucket"

    val s3Adapter = S3FileUploadUrlGeneratorAdapter(s3Presigner, bucketName)

    Given("파일 업로드 URL 생성 요청이 주어졌을 때") {
        val filename = "profile.jpg"
        val domain = FileDomain.INSTRUCTORS
        val domainId = 123L
        val category = FileCategory.PROFILE
        val contentType = "image/jpeg"

        // Mock 객체의 행동 정의
        val mockUrl = URL("https://test-bucket.s3.ap-northeast-2.amazonaws.com/presigned-url")
        val mockPresignedRequest = mockk<PresignedPutObjectRequest>()
        every { mockPresignedRequest.url() } returns mockUrl
        every {
            s3Presigner.presignPutObject(
                any<software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest>()
            )
        } returns mockPresignedRequest

        When("generate 메소드를 실행하면") {
            val result = s3Adapter.generate(filename, domain, domainId, category, contentType)

            Then("올바른 경로와 Pre-signed URL이 포함된 결과가 반환되어야 한다") {
                result.preSignedUrl shouldBe mockUrl.toString()
                result.fileKey shouldContain "instructors/123/profile/"
                result.fileKey shouldEndWith ".jpg"
            }

            Then("S3Presigner가 올바른 파라미터로 호출되어야 한다") {
                verify(exactly = 1) {
                    s3Presigner.presignPutObject(
                        withArg<software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest> {
                            it.putObjectRequest().bucket() shouldBe bucketName
                            it.putObjectRequest().key() shouldBe result.fileKey
                            it.putObjectRequest().contentType() shouldBe contentType
                        }
                    )
                }
            }
        }
    }
})
