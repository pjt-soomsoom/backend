package com.soomsoom.backend.application.service.instructor

import com.soomsoom.backend.application.port.`in`.instructor.command.UpdateInstructorInfoCommand
import com.soomsoom.backend.application.port.`in`.upload.command.ValidatedFileMetadata
import com.soomsoom.backend.application.port.out.instructor.InstructorPort
import com.soomsoom.backend.application.port.out.upload.FileUploadUrlGeneratorPort
import com.soomsoom.backend.application.port.out.upload.dto.FileUploadUrl
import com.soomsoom.backend.common.exception.SoomSoomException
import com.soomsoom.backend.domain.instructor.InstructorErrorCode
import com.soomsoom.backend.domain.instructor.model.Instructor
import com.soomsoom.backend.domain.user.FileCategory
import com.soomsoom.backend.domain.user.FileDomain
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import java.time.LocalDateTime

class UpdateInstructorServiceTest : BehaviorSpec({
    val instructorPort = mockk<InstructorPort>()
    val fileUploadUrlGeneratorPort = mockk<FileUploadUrlGeneratorPort>()
    val updateInstructorService = UpdateInstructorService(
        instructorPort,
        fileUploadUrlGeneratorPort
    )

    afterEach {
        clearAllMocks()
    }

    Given("존재하는 강사 ID와 유효한 수정 정보가 주어졌을 때") {
        val instructorId = 1L
        val command = UpdateInstructorInfoCommand(
            instructorId = instructorId,
            name = "수정된 강사 이름",
            bio = "수정된 소개"
        )
        val originalInstructor = Instructor(
            id = instructorId,
            name = "원래 이름",
            bio = "원래 소개",
            createdAt = LocalDateTime.now()
        )
        val instructorSlot = slot<Instructor>()

        every { instructorPort.findById(instructorId) } returns originalInstructor
        every { instructorPort.save(capture(instructorSlot)) } answers { firstArg() }

        When("updateInfo 메서드를 실행하면") {
            val result = updateInstructorService.updateInfo(command)

            Then("강사 정보가 수정되고, 수정된 정보가 담긴 DTO를 반환해야 한다") {
                verify(exactly = 1) { instructorPort.findById(instructorId) }
                verify(exactly = 1) { instructorPort.save(any()) }

                val capturedInstructor = instructorSlot.captured
                capturedInstructor.name shouldBe command.name
                capturedInstructor.bio shouldBe command.bio

                result.id shouldBe instructorId
                result.name shouldBe command.name
                result.bio shouldBe command.bio
            }
        }
    }

    Given("존재하지 않는 강사 ID가 주어졌을 때") {
        val nonExistentId = 99L
        val command = UpdateInstructorInfoCommand(
            instructorId = nonExistentId,
            name = "수정될 수 없는 이름",
            bio = "수정될 수 없는 소개"
        )

        every { instructorPort.findById(nonExistentId) } returns null

        When("updateInfo 메서드를 실행하면") {
            Then("SoomSoomException(NOT_FOUND) 예외가 발생해야 한다") {
                val exception = shouldThrow<SoomSoomException> {
                    updateInstructorService.updateInfo(command)
                }
                exception.errorCode shouldBe InstructorErrorCode.NOT_FOUND

                verify(exactly = 0) { instructorPort.save(any()) }
            }
        }
    }

    Given("프로필 이미지 URL 업데이트 요청 시, 강사가 존재할 때") {
        val instructorId = 1L
        val fileMetadata = ValidatedFileMetadata("profile.jpg", "image/jpeg")
        val instructor = Instructor(id = instructorId, name = "테스트 강사", bio = "소개", createdAt = LocalDateTime.now())
        // 실제 FileUploadUrl DTO를 사용하여 테스트 데이터를 생성합니다.
        val fileUploadUrl = FileUploadUrl("https://s3.presigned.url/...", "instructors/1/profile/uuid.jpg")

        every { instructorPort.findById(instructorId) } returns instructor
        every {
            fileUploadUrlGeneratorPort.generate(
                filename = fileMetadata.filename,
                domain = FileDomain.INSTRUCTORS,
                domainId = instructorId,
                category = FileCategory.PROFILE,
                contentType = fileMetadata.contentType
            )
        } returns fileUploadUrl // 실제 DTO 객체를 반환하도록 설정합니다.

        When("updateProfileImageUrl 메서드를 실행하면") {
            val result = updateInstructorService.updateProfileImageUrl(instructorId, fileMetadata)

            Then("파일 업로드 URL을 생성하고, 저장 로직 없이 DTO를 반환해야 한다") {
                verify(exactly = 1) { instructorPort.findById(instructorId) }
                verify(exactly = 1) { fileUploadUrlGeneratorPort.generate(any(), any(), any(), any(), any()) }
                verify(exactly = 0) { instructorPort.save(any()) }

                // 반환된 DTO의 정보가 올바른지 확인합니다.
                result.instructorId shouldBe instructorId
                result.preSignedUrl shouldBe fileUploadUrl.preSignedUrl
                result.fileKey shouldBe fileUploadUrl.fileKey
            }
        }
    }

    Given("프로필 이미지 URL 업데이트 요청 시, 강사가 존재하지 않을 때") {
        val nonExistentId = 99L
        val fileMetadata = ValidatedFileMetadata("profile.jpg", "image/jpeg")

        every { instructorPort.findById(nonExistentId) } returns null

        When("updateProfileImageUrl 메서드를 실행하면") {
            Then("SoomSoomException(NOT_FOUND) 예외가 발생해야 한다") {
                val exception = shouldThrow<SoomSoomException> {
                    updateInstructorService.updateProfileImageUrl(nonExistentId, fileMetadata)
                }
                exception.errorCode shouldBe InstructorErrorCode.NOT_FOUND

                verify(exactly = 0) { fileUploadUrlGeneratorPort.generate(any(), any(), any(), any(), any()) }
                verify(exactly = 0) { instructorPort.save(any()) }
            }
        }
    }
})
