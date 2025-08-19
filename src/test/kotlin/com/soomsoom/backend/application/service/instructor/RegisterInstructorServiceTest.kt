package com.soomsoom.backend.application.service.instructor

import com.soomsoom.backend.application.port.`in`.instructor.command.CompleteImageUploadCommand
import com.soomsoom.backend.application.port.`in`.instructor.command.RegisterInstructorCommand
import com.soomsoom.backend.application.port.`in`.upload.command.ValidatedFileMetadata
import com.soomsoom.backend.application.port.out.instructor.InstructorPort
import com.soomsoom.backend.application.port.out.upload.FileDeleterPort
import com.soomsoom.backend.application.port.out.upload.FileUrlResolverPort
import com.soomsoom.backend.application.port.out.upload.FileValidatorPort
import com.soomsoom.backend.application.port.out.upload.dto.FileUploadUrl
import com.soomsoom.backend.application.service.instructor.command.RegisterInstructorService
import com.soomsoom.backend.application.service.upload.FileUploadFacade
import com.soomsoom.backend.application.service.upload.GenerateUploadUrlsRequest
import com.soomsoom.backend.common.exception.SoomSoomException
import com.soomsoom.backend.domain.instructor.model.Instructor
import com.soomsoom.backend.domain.upload.UploadErrorCode
import com.soomsoom.backend.domain.user.FileCategory
import com.soomsoom.backend.domain.user.FileDomain
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.runs
import io.mockk.slot
import io.mockk.verify
import java.time.LocalDateTime

class RegisterInstructorServiceTest : BehaviorSpec({

    val instructorPort = mockk<InstructorPort>()
    val fileUploadFacade = mockk<FileUploadFacade>()
    val fileUrlResolverPort = mockk<FileUrlResolverPort>()
    val fileDeleterPort = mockk<FileDeleterPort>()
    val fileValidatorPort = mockk<FileValidatorPort>()

    val registerInstructorService = RegisterInstructorService(
        instructorPort,
        fileUploadFacade,
        fileUrlResolverPort,
        fileDeleterPort,
        fileValidatorPort
    )

    afterEach {
        clearAllMocks()
    }

    Given("이미지가 포함된 강사 등록 정보가 주어졌을 때") {
        val command = RegisterInstructorCommand(
            name = "박상훈",
            bio = "초보 명상가",
            profileImageMetadata = ValidatedFileMetadata(
                "profile.jpeg",
                "image/jpeg"
            )
        )

        val savedInstructor = Instructor(1L, name = command.name, bio = command.bio, createdAt = LocalDateTime.now())
        val expectedUploadUrl = FileUploadUrl("https://s3.pre.signed.url/...", "instructors/1/profile/uuid.jpg")
        val requestSlot = slot<GenerateUploadUrlsRequest>()

        every { instructorPort.save(any()) } returns savedInstructor
        every { fileUploadFacade.generateUploadUrls(capture(requestSlot)) } returns mapOf(FileCategory.PROFILE to expectedUploadUrl)

        When("register 메서드를 실행하면") {
            val result = registerInstructorService.register(command)

            Then("강사 정보가 저장되고, Pre-signed URL이 포함된 결과가 반환되어야 한다.") {
                result.instructorId shouldBe 1L
                result.preSignedUrl shouldBe expectedUploadUrl.preSignedUrl
                result.fileKey shouldBe expectedUploadUrl.fileKey

                verify(exactly = 1) { instructorPort.save(any()) }
                verify(exactly = 1) { fileUploadFacade.generateUploadUrls(any()) }

                val capturedRequest = requestSlot.captured
                capturedRequest.domain shouldBe FileDomain.INSTRUCTORS
                capturedRequest.domainId shouldBe savedInstructor.id!!
                capturedRequest.files.size shouldBe 1
                capturedRequest.files[0].category shouldBe FileCategory.PROFILE
                capturedRequest.files[0].metadata shouldBe command.profileImageMetadata!!
            }
        }
    }

    Given("이미지가 포함되지 않은 강사 등록 정보가 주어졌을 때") {
        val command = RegisterInstructorCommand(
            name = "박상훈",
            bio = "초보 명상가",
            profileImageMetadata = null
        )

        val savedInstructor = Instructor(1L, name = command.name, bio = command.bio, createdAt = LocalDateTime.now())

        every { instructorPort.save(any()) } returns savedInstructor

        When("register 메서드를 실행하면") {
            val result = registerInstructorService.register(command)

            Then("강사 정보는 저장되고, URL 관련 정보는 null 이어야 한다") {
                result.instructorId shouldBe savedInstructor.id
                result.preSignedUrl shouldBe null
                result.fileKey shouldBe null

                verify(exactly = 0) { fileUploadFacade.generateUploadUrls(any()) }
            }
        }
    }

    Given("유효한 업로드 완료 정보가 주어졌고, 기존 프로필 이미지가 있을 때") {
        val command = CompleteImageUploadCommand(instructorId = 1L, fileKey = "instructors/1/profile/new.jpg")
        val oldFileKey = "instructors/1/profile/old.jpg"
        val existingInstructor = Instructor(1L, "박상훈", bio = "소개", profileImageFileKey = oldFileKey)
        val resolvedUrl = "https://base_url/instructors/1/profile/new.jpg"

        every { fileValidatorPort.validate(command.fileKey) } returns true
        every { instructorPort.findById(command.instructorId) } returns existingInstructor
        every { fileUrlResolverPort.resolve(command.fileKey) } returns resolvedUrl
        every { fileDeleterPort.delete(oldFileKey) } just runs
        every { instructorPort.save(any()) } returns mockk<Instructor>()

        When("completeImageUpload 메서드를 실행하면") {
            registerInstructorService.completeImageUpload(command)

            Then("파일 유효성 검사, 강사 조회, URL 생성, 강사 정보 업데이트, 이전 파일 삭제, 저장을 순서대로 수행해야 한다") {
                verify(exactly = 1) { fileValidatorPort.validate(command.fileKey) }
                verify(exactly = 1) { instructorPort.findById(command.instructorId) }
                verify(exactly = 1) { fileUrlResolverPort.resolve(command.fileKey) }
                verify(exactly = 1) { fileDeleterPort.delete(oldFileKey) }
                verify(exactly = 1) {
                    instructorPort.save(
                        withArg {
                            it.profileImageUrl shouldBe resolvedUrl
                            it.profileImageFileKey shouldBe command.fileKey
                        }
                    )
                }
            }
        }
    }

    Given("유효한 업로드 완료 정보가 주어졌고, 기존 프로필 이미지가 없을 때") {
        val command = CompleteImageUploadCommand(instructorId = 1L, fileKey = "instructors/1/profile/new.jpg")
        val existingInstructor = Instructor(1L, "박상훈", bio = "소개", profileImageFileKey = null)
        val resolvedUrl = "https://base_url/instructors/1/profile/new.jpg"

        every { fileValidatorPort.validate(command.fileKey) } returns true
        every { instructorPort.findById(command.instructorId) } returns existingInstructor
        every { fileUrlResolverPort.resolve(command.fileKey) } returns resolvedUrl
        every { instructorPort.save(any()) } returns mockk<Instructor>()

        When("completeImageUpload 메서드를 실행하면") {
            registerInstructorService.completeImageUpload(command)

            Then("이전 파일이 없으므로 파일 삭제 로직은 호출되지 않아야 한다") {
                verify(exactly = 1) { fileValidatorPort.validate(command.fileKey) }
                verify(exactly = 1) { instructorPort.findById(command.instructorId) }
                verify(exactly = 1) { fileUrlResolverPort.resolve(command.fileKey) }
                verify(exactly = 0) { fileDeleterPort.delete(any()) }
                verify(exactly = 1) { instructorPort.save(any()) }
            }
        }
    }

    Given("유효하지 않은 파일 키로 업로드 완료를 요청했을 때") {
        val command = CompleteImageUploadCommand(instructorId = 1L, fileKey = "invalid-key")

        every { fileValidatorPort.validate(command.fileKey) } returns false

        When("completeImageUpload 메서드를 실행하면") {
            Then("SoomSoomException(FILE_KEY_MISMATCH) 예외가 발생해야 한다") {
                val exception = shouldThrow<SoomSoomException> {
                    registerInstructorService.completeImageUpload(command)
                }
                exception.errorCode shouldBe UploadErrorCode.FILE_KEY_MISMATCH

                verify(exactly = 0) { instructorPort.findById(any()) }
                verify(exactly = 0) { instructorPort.save(any()) }
            }
        }
    }
})
