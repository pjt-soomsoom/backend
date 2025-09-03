package com.soomsoom.backend.application.service.activity.command

import com.soomsoom.backend.application.port.`in`.activity.command.CompleteActivityUploadCommand
import com.soomsoom.backend.application.port.`in`.activity.command.CreateBreathingActivityCommand
import com.soomsoom.backend.application.port.`in`.upload.command.ValidatedFileMetadata
import com.soomsoom.backend.application.port.out.activity.ActivityPort
import com.soomsoom.backend.application.port.out.instructor.InstructorPort
import com.soomsoom.backend.application.port.out.upload.FileDeleterPort
import com.soomsoom.backend.application.port.out.upload.FileUrlResolverPort
import com.soomsoom.backend.application.port.out.upload.FileValidatorPort
import com.soomsoom.backend.application.port.out.upload.dto.FileUploadUrl
import com.soomsoom.backend.application.service.upload.FileUploadFacade
import com.soomsoom.backend.common.exception.SoomSoomException
import com.soomsoom.backend.domain.activity.model.BreathingActivity
import com.soomsoom.backend.domain.instructor.InstructorErrorCode
import com.soomsoom.backend.domain.instructor.model.Instructor
import com.soomsoom.backend.domain.upload.type.FileCategory
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.runs
import io.mockk.verify

class CreateActivityServiceTest : BehaviorSpec({
    val activityPort = mockk<ActivityPort>()
    val instructorPort = mockk<InstructorPort>()
    val fileUploadFacade = mockk<FileUploadFacade>()
    val fileUrlResolverPort = mockk<FileUrlResolverPort>()
    val fileDeleterPort = mockk<FileDeleterPort>()
    val fileValidatorPort = mockk<FileValidatorPort>()

    val createActivityService = CreateActivityService(
        activityPort,
        instructorPort,
        fileUrlResolverPort,
        fileDeleterPort,
        fileValidatorPort,
        fileUploadFacade
    )

    afterEach { clearAllMocks() }

    Given("유효한 Activity 생성 정보가 주어졌을 때") {
        val command = CreateBreathingActivityCommand(
            title = "새로운 호흡 명상",
            descriptions = listOf("설명"),
            authorId = 1L,
            narratorId = 2L,
            durationInSeconds = 120,
            thumbnailImageMetadata = ValidatedFileMetadata("thumb.jpg", "image/jpeg"),
            audioMetadata = ValidatedFileMetadata("audio.mp3", "audio/mpeg"),
            timeline = emptyList()
        )
        val savedActivity = BreathingActivity(
            id = 10L,
            title = command.title,
            descriptions = command.descriptions,
            authorId = command.authorId,
            narratorId = command.narratorId,
            durationInSeconds = command.durationInSeconds,
            timeline = command.timeline,
            thumbnailImageUrl = null,
            thumbnailFileKey = null,
            audioUrl = null,
            audioFileKey = null
        )
        val mockAuthor = mockk<Instructor>()
        val mockNarrator = mockk<Instructor>()
        val uploadUrls = mapOf(
            FileCategory.THUMBNAIL to FileUploadUrl("thumb-url", "thumb-key"),
            FileCategory.AUDIO to FileUploadUrl("audio-url", "audio-key")
        )

        every { instructorPort.findById(1L) } returns mockAuthor
        every { instructorPort.findById(2L) } returns mockNarrator
        every { activityPort.save(any()) } returns savedActivity
        every { fileUploadFacade.generateUploadUrls(any()) } returns uploadUrls

        When("create 메서드를 실행하면") {
            val result = createActivityService.create(command)

            Then("강사 존재 여부를 확인하고, Activity를 저장한 뒤, 파일 업로드 URL을 생성하여 반환해야 한다") {
                verify(exactly = 1) { instructorPort.findById(1L) }
                verify(exactly = 1) { instructorPort.findById(2L) }
                verify(exactly = 1) { activityPort.save(any()) }
                verify(exactly = 1) { fileUploadFacade.generateUploadUrls(any()) }

                result.activityId shouldBe savedActivity.id
                result.thumbnailUploadInfo.preSignedUrl shouldBe "thumb-url"
                result.audioUploadInfo.fileKey shouldBe "audio-key"
            }
        }
    }

    Given("존재하지 않는 강사 ID로 Activity 생성을 요청하면") {
        val command = CreateBreathingActivityCommand(
            title = "잘못된 명상",
            descriptions = listOf("설명"),
            authorId = 99L,
            narratorId = 2L,
            durationInSeconds = 120,
            thumbnailImageMetadata = mockk(),
            audioMetadata = mockk(),
            timeline = emptyList()
        )
        every { instructorPort.findById(99L) } returns null
        every { instructorPort.findById(2L) } returns mockk()

        When("create 메서드를 실행하면") {
            Then("NOT_FOUND 예외가 발생해야 한다") {
                val exception = shouldThrow<SoomSoomException> {
                    createActivityService.create(command)
                }
                exception.errorCode shouldBe InstructorErrorCode.NOT_FOUND
            }
        }
    }

    Given("유효한 파일 업로드 완료 정보가 주어졌을 때") {
        val command = CompleteActivityUploadCommand(10L, "thumb-key", "audio-key")
        val existingActivity = mockk<BreathingActivity>(relaxed = true)

        every { fileValidatorPort.validate(any()) } returns true
        every { activityPort.findById(10L) } returns existingActivity
        every { fileUrlResolverPort.resolve("thumb-key") } returns "resolved-thumb-url"
        every { fileUrlResolverPort.resolve("audio-key") } returns "resolved-audio-url"
        every { activityPort.save(any()) } returns mockk()
        every { fileDeleterPort.delete(any()) } just runs

        When("completeUpload 메서드를 실행하면") {
            createActivityService.completeUpload(command)

            Then("파일 유효성을 검사하고, Activity의 이미지 및 오디오 URL을 업데이트하고 저장해야 한다") {
                verify(exactly = 1) { fileValidatorPort.validate("thumb-key") }
                verify(exactly = 1) { fileValidatorPort.validate("audio-key") }
                verify(exactly = 1) { activityPort.findById(10L) }
                verify(exactly = 1) { existingActivity.updateThumbnailImage("resolved-thumb-url", "thumb-key") }
                verify(exactly = 1) { existingActivity.updateAudio("resolved-audio-url", "audio-key") }
                verify(exactly = 1) { activityPort.save(existingActivity) }
            }
        }
    }
})
