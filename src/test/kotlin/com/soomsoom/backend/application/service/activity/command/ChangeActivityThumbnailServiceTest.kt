package com.soomsoom.backend.application.service.activity.command

import com.soomsoom.backend.adapter.out.persistence.activity.repository.jpa.dto.ActivityWithInstructorsDto
import com.soomsoom.backend.adapter.out.persistence.activity.repository.jpa.entity.MeditationActivityJpaEntity
import com.soomsoom.backend.adapter.out.persistence.instructor.repository.jpa.entity.InstructorJpaEntity
import com.soomsoom.backend.application.port.`in`.activity.command.ChangeActivityThumbnailCommand
import com.soomsoom.backend.application.port.`in`.activity.command.CompleteActivityThumbnailChangeCommand
import com.soomsoom.backend.application.port.`in`.upload.command.ValidatedFileMetadata
import com.soomsoom.backend.application.port.out.activity.ActivityPort
import com.soomsoom.backend.application.port.out.upload.FileDeleterPort
import com.soomsoom.backend.application.port.out.upload.FileUrlResolverPort
import com.soomsoom.backend.application.port.out.upload.FileValidatorPort
import com.soomsoom.backend.application.port.out.upload.dto.FileUploadUrl
import com.soomsoom.backend.application.service.upload.FileUploadFacade
import com.soomsoom.backend.common.exception.SoomSoomException
import com.soomsoom.backend.domain.activity.ActivityErrorCode
import com.soomsoom.backend.domain.activity.model.MeditationActivity
import com.soomsoom.backend.domain.upload.UploadErrorCode
import com.soomsoom.backend.domain.upload.type.FileCategory
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.runs
import io.mockk.spyk
import io.mockk.verify
import java.time.LocalDateTime

class ChangeActivityThumbnailServiceTest : BehaviorSpec({
    // --- Mock 객체 준비 ---
    val activityPort = mockk<ActivityPort>()
    val fileUploadFacade = mockk<FileUploadFacade>()
    val fileUrlResolverPort = mockk<FileUrlResolverPort>()
    val fileDeleterPort = mockk<FileDeleterPort>()
    val fileValidatorPort = mockk<FileValidatorPort>()

    // --- 테스트 대상 서비스 생성 ---
    val changeActivityThumbnailService = ChangeActivityThumbnailService(
        activityPort,
        fileUploadFacade,
        fileUrlResolverPort,
        fileDeleterPort,
        fileValidatorPort
    )

    afterEach { clearAllMocks() }

    // --- changeThumbnail 메서드 테스트 ---
    Given("썸네일 교체를 위한 Presigned URL 요청 시") {
        val activityId = 1L
        val command = ChangeActivityThumbnailCommand(
            activityId = activityId,
            thumbnailImageMetadata = ValidatedFileMetadata("new_thumb.jpg", "image/jpeg")
        )
        val uploadUrl = FileUploadUrl("https://presigned-url.com/...", "new-thumb-key")

        When("Activity가 존재하면") {
            // Mocking: activityPort가 activity를 찾고, fileUploadFacade가 URL을 생성하도록 설정
            every { activityPort.findById(activityId) } returns mockk<MeditationActivity>()
            every { fileUploadFacade.generateUploadUrls(any()) } returns mapOf(FileCategory.THUMBNAIL to uploadUrl)

            val result = changeActivityThumbnailService.changeThumbnail(command)

            Then("Presigned URL과 파일 키를 담은 DTO를 반환해야 한다") {
                verify(exactly = 1) { activityPort.findById(activityId) }
                verify(exactly = 1) { fileUploadFacade.generateUploadUrls(any()) }

                result.preSignedUrl shouldBe uploadUrl.preSignedUrl
                result.fileKey shouldBe uploadUrl.fileKey
            }
        }

        When("Activity가 존재하지 않으면") {
            // Mocking: activityPort가 null을 반환하도록 설정
            every { activityPort.findById(activityId) } returns null

            Then("NOT_FOUND 예외가 발생해야 한다") {
                val exception = shouldThrow<SoomSoomException> {
                    changeActivityThumbnailService.changeThumbnail(command)
                }
                exception.errorCode shouldBe ActivityErrorCode.NOT_FOUND
            }
        }
    }

    // --- completeThumbnailChange 메서드 테스트 ---
    Given("썸네일 교체 완료 요청 시") {
        val activityId = 1L
        val newFileKey = "new-thumb-key"
        val newFileUrl = "https://resolved.com/new-thumb-key"

        When("유효한 파일 키로 교체를 완료하면 (기존 썸네일 파일 존재)") {
            val oldFileKey = "old-thumb-key"
            val command = CompleteActivityThumbnailChangeCommand(activityId, newFileKey)
            val existingActivity = spyk(
                MeditationActivity(
                    id = activityId, title = "명상", descriptions = listOf("설명"), authorId = 1, narratorId = 2,
                    durationInSeconds = 120, audioUrl = null, audioFileKey = null,
                    thumbnailImageUrl = "old-url", thumbnailFileKey = oldFileKey
                )
            )

            // toActivityResult() 매퍼가 정상 동작하도록 실제 JPA 엔티티를 Mocking
            val mockActivityEntity = mockk<MeditationActivityJpaEntity>(relaxed = true)
            every { mockActivityEntity.createdAt } returns LocalDateTime.now()
            val mockAuthorEntity = mockk<InstructorJpaEntity>(relaxed = true)
            every { mockAuthorEntity.createdAt } returns LocalDateTime.now()
            val mockNarratorEntity = mockk<InstructorJpaEntity>(relaxed = true)
            every { mockNarratorEntity.createdAt } returns LocalDateTime.now()
            val mockDto = ActivityWithInstructorsDto(mockActivityEntity, mockAuthorEntity, mockNarratorEntity)

            // Mocking
            every { fileValidatorPort.validate(newFileKey) } returns true
            every { activityPort.findById(activityId) } returns existingActivity
            every { fileUrlResolverPort.resolve(newFileKey) } returns newFileUrl
            every { fileDeleterPort.delete(oldFileKey) } just runs
            every { activityPort.save(any()) } returns mockk()
            every { activityPort.findByIdWithInstructors(activityId) } returns mockDto

            changeActivityThumbnailService.completeThumbnailChange(command)

            Then("파일 유효성 검사, URL 해석, 기존 파일 삭제, Activity 저장 순으로 호출되어야 한다") {
                verify(exactly = 1) { fileValidatorPort.validate(newFileKey) }
                verify(exactly = 1) { activityPort.findById(activityId) }
                verify(exactly = 1) { fileUrlResolverPort.resolve(newFileKey) }
                verify(exactly = 1) { existingActivity.updateThumbnailImage(newFileUrl, newFileKey) }
                verify(exactly = 1) { fileDeleterPort.delete(oldFileKey) }
                verify(exactly = 1) { activityPort.save(existingActivity) }
                verify(exactly = 1) { activityPort.findByIdWithInstructors(activityId) }
            }
        }

        When("유효한 파일 키로 교체를 완료하면 (기존 썸네일 파일 없음)") {
            val command = CompleteActivityThumbnailChangeCommand(activityId, newFileKey)
            val existingActivity = spyk(
                MeditationActivity(
                    id = activityId, title = "명상", descriptions = listOf("설명"), authorId = 1, narratorId = 2,
                    durationInSeconds = 120, audioUrl = null, audioFileKey = null,
                    thumbnailImageUrl = null, thumbnailFileKey = null // 기존 파일 없음
                )
            )
            val mockActivityEntity = mockk<MeditationActivityJpaEntity>(relaxed = true)
            every { mockActivityEntity.createdAt } returns LocalDateTime.now()
            val mockDto = ActivityWithInstructorsDto(mockActivityEntity, mockk(relaxed = true), mockk(relaxed = true))

            // Mocking
            every { fileValidatorPort.validate(newFileKey) } returns true
            every { activityPort.findById(activityId) } returns existingActivity
            every { fileUrlResolverPort.resolve(newFileKey) } returns newFileUrl
            every { activityPort.save(any()) } returns mockk()
            every { activityPort.findByIdWithInstructors(activityId) } returns mockDto

            changeActivityThumbnailService.completeThumbnailChange(command)

            Then("기존 파일이 없으므로 파일 삭제 로직은 호출되지 않아야 한다") {
                verify(exactly = 0) { fileDeleterPort.delete(any()) }
            }
        }

        When("유효하지 않은 파일 키로 교체를 완료하면") {
            val command = CompleteActivityThumbnailChangeCommand(activityId, newFileKey)
            // Mocking: 파일 유효성 검사 실패
            every { fileValidatorPort.validate(newFileKey) } returns false

            Then("FILE_KEY_MISMATCH 예외가 발생해야 한다") {
                val exception = shouldThrow<SoomSoomException> {
                    changeActivityThumbnailService.completeThumbnailChange(command)
                }
                exception.errorCode shouldBe UploadErrorCode.FILE_KEY_MISMATCH
            }
        }
    }
})
