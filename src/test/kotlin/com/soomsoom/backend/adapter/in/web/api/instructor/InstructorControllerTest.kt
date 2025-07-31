package com.soomsoom.backend.adapter.`in`.web.api.instructor

import com.fasterxml.jackson.databind.ObjectMapper
import com.ninjasquad.springmockk.MockkBean
import com.soomsoom.backend.adapter.`in`.web.api.instructor.request.RegisterInstructorRequest
import com.soomsoom.backend.adapter.`in`.web.api.instructor.request.toCommand
import com.soomsoom.backend.adapter.`in`.web.api.upload.request.FileMetadata
import com.soomsoom.backend.application.port.`in`.instructor.dto.RegisterInstructorResult
import com.soomsoom.backend.application.port.`in`.instructor.usecase.RegisterInstructorUseCase
import io.kotest.core.spec.style.BehaviorSpec
import io.mockk.every
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.post

@WebMvcTest(
    controllers = [InstructorController::class],
    excludeAutoConfiguration = [SecurityAutoConfiguration::class]
)
class InstructorControllerTest(
    private val mockMvc: MockMvc,
    private val objectMapper: ObjectMapper,
    @MockkBean private val registerInstructorUseCase: RegisterInstructorUseCase,
) : BehaviorSpec({

    Given("강사 등록 API(/api/instructors)에 대한 요청이 주어졌을 때") {
        When("유요한 데이터로 강사 등록을 요청하면") {
            val request = RegisterInstructorRequest(
                name = "테스트 강사",
                bio = "소개입니다.",
                profileImageMetadata = FileMetadata(filename = "profile.jpg", contentType = "image/jpeg")
            )

            val expectedResult = RegisterInstructorResult(
                instructorId = 1L,
                preSignedUrl = "https://s3.pre.signed.url/...",
                fileKey = "instructors/1/profile/uuid.jpg"
            )

            every { registerInstructorUseCase.register(request.toCommand()) } returns expectedResult

            Then("201 OK 상태 코드와 함께 등록 결과를 반환해야 한다") {
                mockMvc.post("/instructors") {
                    contentType = MediaType.APPLICATION_JSON
                    content = objectMapper.writeValueAsString(request)
                }.andExpect {
                    status { isCreated() }
                    jsonPath("$.instructorId") { value(1L) }
                    jsonPath("$.preSignedUrl") { value(expectedResult.preSignedUrl) }
                    jsonPath("$.fileKey") { value(expectedResult.fileKey) }
                }
            }
        }
    }
})
