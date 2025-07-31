package com.soomsoom.backend.application.service.instructor

import com.soomsoom.backend.domain.user.FileCategory
import com.soomsoom.backend.domain.user.FileDomain
import com.soomsoom.backend.application.port.`in`.instructor.command.CompleteImageUploadCommand
import com.soomsoom.backend.application.port.`in`.instructor.command.RegisterInstructorCommand
import com.soomsoom.backend.application.port.`in`.upload.command.ValidatedFileMetadata
import com.soomsoom.backend.application.port.out.instructor.InstructorPort
import com.soomsoom.backend.application.port.out.upload.FileUploadUrlGeneratorPort
import com.soomsoom.backend.application.port.out.upload.FileUrlResolverPort
import com.soomsoom.backend.application.port.out.upload.dto.FileUploadUrl
import com.soomsoom.backend.domain.instructor.model.Instructor
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify

class RegisterInstructorServiceTest : BehaviorSpec({

    val instructorPort = mockk<InstructorPort>()
    val fileUploadUrlGeneratorPort = mockk<FileUploadUrlGeneratorPort>()
    val fileUrlResolverPort = mockk<FileUrlResolverPort>()

    val registerInstructorService = RegisterInstructorService(
        instructorPort,
        fileUploadUrlGeneratorPort,
        fileUrlResolverPort
    )

    afterEach {
        clearMocks(instructorPort, fileUploadUrlGeneratorPort, fileUrlResolverPort)
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

        val savedInstructor = Instructor(1L, name = command.name, bio = command.bio)
        val expectedUploadUrl = FileUploadUrl("https://s3.pre.signed.url/...", "instructors/1/profile/uuid.jpg")

        every { instructorPort.save(any()) } returns savedInstructor
        every { fileUploadUrlGeneratorPort.generate(any(), any(), any(), any(), any()) } returns expectedUploadUrl

        When("register 메서드를 실행하면") {
            val result = registerInstructorService.register(command)

            Then("강사 정보가 저장되고, Pre-signed URL이 포함된 결과가 반환되어야 한다.") {
                result.instructorId shouldBe 1L
                result.preSignedUrl shouldBe expectedUploadUrl.preSignedUrl

                verify(exactly = 1) { instructorPort.save(any()) }
                verify(exactly = 1) {
                    fileUploadUrlGeneratorPort.generate(
                        command.profileImageMetadata!!.filename,
                        FileDomain.INSTRUCTORS,
                        savedInstructor.id!!,
                        FileCategory.PROFILE,
                        command.profileImageMetadata!!.contentType
                    )
                }
            }
        }
    }

    Given("이미자가 포함되지 않은 강사 등록 정보가 주어졌을 때") {
        val command = RegisterInstructorCommand(
            name = "박상훈",
            bio = "초보 명상가",
            profileImageMetadata = null
        )

        val savedInstructor = Instructor(1L, name = command.name, bio = command.bio)

        every { instructorPort.save(any()) } returns savedInstructor

        When("register 메서드를 실행하면") {
            val result = registerInstructorService.register(command)

            Then("강사 정보는 저장되고, URL 관련 정보는 null 이어야 한다") {
                result.instructorId shouldBe savedInstructor.id
                result.preSignedUrl shouldBe null
                result.fileKey shouldBe null

                verify(exactly = 0) { fileUploadUrlGeneratorPort.generate(any(), any(), any(), any(), any()) }
            }
        }
    }

    Given("유효한 업로드 완료 정보가 주어졌을 때") {
        val command = CompleteImageUploadCommand(instructorId = 1L, fileKey = "instructors/1/profile/uuid.jpg")
        val existingInstructor = Instructor(1L, "박상훈", bio = "소개")
        val resolvedUrl = "https://base_url/instructors/1/profile/uuid.jpg"

        every { instructorPort.findById(command.instructorId) } returns existingInstructor
        every { fileUrlResolverPort.resolve(command.fileKey) } returns resolvedUrl
        every { instructorPort.save(any()) } returns mockk<Instructor>()

        When("completeImageUpload 메서드를 실행하면") {
            registerInstructorService.completeImageUpload(command)

            Then("강사를 조회하고, 파일 URL을 만들어 강사의 profileImageUrl을 업데이트 해야 한다") {
                verify(exactly = 1) { instructorPort.findById(command.instructorId) }
                verify(exactly = 1) { fileUrlResolverPort.resolve(command.fileKey) }
                verify(exactly = 1) {
                    instructorPort.save(
                        withArg {
                            it.profileImageUrl shouldBe resolvedUrl
                        }
                    )
                }
            }
        }
    }
})
