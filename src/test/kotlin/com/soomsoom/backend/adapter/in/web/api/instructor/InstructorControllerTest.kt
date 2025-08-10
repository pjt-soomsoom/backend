package com.soomsoom.backend.adapter.`in`.web.api.instructor

import com.fasterxml.jackson.databind.ObjectMapper
import com.ninjasquad.springmockk.MockkBean
import com.soomsoom.backend.adapter.`in`.security.config.SecurityConfig
import com.soomsoom.backend.adapter.`in`.security.provider.JwtTokenProvider
import com.soomsoom.backend.adapter.`in`.web.api.common.DeletionStatus
import com.soomsoom.backend.adapter.`in`.web.api.instructor.config.InstructorConfigurer
import com.soomsoom.backend.adapter.`in`.web.api.instructor.request.InstructorSearchCriteria
import com.soomsoom.backend.adapter.`in`.web.api.instructor.request.RegisterInstructorRequest
import com.soomsoom.backend.adapter.`in`.web.api.instructor.request.UpdateInstructorInfoRequest
import com.soomsoom.backend.adapter.`in`.web.api.instructor.request.toCommand
import com.soomsoom.backend.adapter.`in`.web.api.upload.request.FileMetadata
import com.soomsoom.backend.application.port.`in`.instructor.dto.FindInstructorResult
import com.soomsoom.backend.application.port.`in`.instructor.dto.RegisterInstructorResult
import com.soomsoom.backend.application.port.`in`.instructor.usecase.DeleteInstructorUseCase
import com.soomsoom.backend.application.port.`in`.instructor.usecase.FindInstructorByIdUseCase
import com.soomsoom.backend.application.port.`in`.instructor.usecase.RegisterInstructorUseCase
import com.soomsoom.backend.application.port.`in`.instructor.usecase.SearchInstructorUseCase
import com.soomsoom.backend.application.port.`in`.instructor.usecase.UpdateInstructorInfoUseCase
import com.soomsoom.backend.application.port.`in`.instructor.usecase.UpdateInstructorProfileImageUrlUseCase
import com.soomsoom.backend.application.port.`in`.upload.command.ValidatedFileMetadata
import com.soomsoom.backend.fixture.TestUserFixture
import io.kotest.core.spec.style.BehaviorSpec
import io.mockk.every
import io.mockk.just
import io.mockk.runs
import io.mockk.verify
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.context.annotation.Import
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.http.MediaType
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.delete
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.patch
import org.springframework.test.web.servlet.post
import java.time.LocalDateTime

@WebMvcTest(
    controllers = [InstructorController::class]
)
@Import(SecurityConfig::class, InstructorConfigurer::class)
class InstructorControllerTest(
    @MockkBean private val registerInstructorUseCase: RegisterInstructorUseCase,
    @MockkBean private val findInstructorByIdUseCase: FindInstructorByIdUseCase,
    @MockkBean private val searchInstructorUseCase: SearchInstructorUseCase,
    @MockkBean private val deleteInstructorUseCase: DeleteInstructorUseCase,
    @MockkBean private val updateInstructorInfoUseCase: UpdateInstructorInfoUseCase,
    @MockkBean private val updateInstructorProfileImageUrlUseCase: UpdateInstructorProfileImageUrlUseCase,

    @MockkBean private val jwtTokenProvider: JwtTokenProvider,

    private val mockMvc: MockMvc,
    private val objectMapper: ObjectMapper,
) : BehaviorSpec({
    Given("강사 등록 API (/instructors)") {
        val now = LocalDateTime.now()
        val request = RegisterInstructorRequest(
            name = "테스트 강사",
            bio = "소개입니다.",
            profileImageMetadata = FileMetadata(filename = "profile.jpg", contentType = "image/jpeg")
        )
        val expectedResult = RegisterInstructorResult(
            instructorId = 1L,
            name = request.name!!,
            bio = request.bio,
            preSignedUrl = "https://s3.pre.signed.url/...",
            fileKey = "instructors/1/profile/uuid.jpg",
            createdAt = now
        )

        When("ADMIN 권한을 가진 사용자가 등록을 요청하면") {
            every { registerInstructorUseCase.register(request.toCommand()) } returns expectedResult

            Then("201 Created 상태 코드와 함께 등록 결과를 반환해야 한다") {
                mockMvc.post("/instructors") {
                    with(user(TestUserFixture.ADMIN))
                    with(csrf())
                    contentType = MediaType.APPLICATION_JSON
                    content = objectMapper.writeValueAsString(request)
                }.andExpect {
                    status { isCreated() }
                    jsonPath("$.instructorId") { value(1L) }
                }
            }
        }

        When("USER 권한을 가진 사용자가 등록을 요청하면") {
            Then("403 Forbidden 에러가 발생해야 한다") {
                mockMvc.post("/instructors") {
                    with(user(TestUserFixture.USER))
                    with(csrf())
                    contentType = MediaType.APPLICATION_JSON
                    content = objectMapper.writeValueAsString(request)
                }.andExpect {
                    status { isForbidden() }
                }
            }
        }
    }

    Given("강사 조회 API") {
        val instructorId = 1L
        val now = LocalDateTime.now()
        val expectedResult = FindInstructorResult(
            id = instructorId,
            name = "김강사",
            bio = "소개",
            profileImageUrl = null,
            createdAt = now,
            modifiedAt = now,
            deletedAt = null
        )

        When("권한과 상관없이 활성 강사 조회를 요청하면") {
            every { findInstructorByIdUseCase.findById(instructorId, DeletionStatus.ACTIVE) } returns expectedResult

            Then("200 OK 상태 코드와 함께 강사 정보를 반환해야 한다") {
                mockMvc.get("/instructors/$instructorId?deletionStatus=ACTIVE") {
                    with(user(TestUserFixture.USER)) // 일반 유저로 테스트
                }.andExpect {
                    status { isOk() }
                    jsonPath("$.id") { value(instructorId) }
                }
            }
        }
    }

    Given("강사 목록 검색 API (/instructors)") {
        val searchCriteria = InstructorSearchCriteria(deletionStatus = DeletionStatus.ACTIVE)
        val pageable: Pageable = PageRequest.of(0, 20)
        val now = LocalDateTime.now()

        val searchResultList = listOf(
            FindInstructorResult(
                id = 1L,
                name = "김테스트강사",
                bio = "소개1",
                profileImageUrl = null,
                createdAt = now,
                modifiedAt = now,
                deletedAt = null
            ),
            FindInstructorResult(
                id = 2L,
                name = "이유명강사",
                bio = "소개2",
                profileImageUrl = null,
                createdAt = now,
                modifiedAt = now,
                deletedAt = null
            )
        )
        val pagedResults = PageImpl(searchResultList, pageable, searchResultList.size.toLong())

        When("활성 상태의 강사 목록 조회를 요청하면") {
            every { searchInstructorUseCase.search(searchCriteria, any()) } returns pagedResults

            Then("200 OK 상태 코드와 함께 페이징된 강사 목록을 반환해야 한다") {
                mockMvc.get("/instructors") {
                    param("deletionStatus", "ACTIVE")
                    with(user(TestUserFixture.USER))
                }.andExpect {
                    status { isOk() }
                    jsonPath("$.content") { isArray() }
                    jsonPath("$.content.length()") { value(2) }
                    jsonPath("$.totalElements") { value(2L) }
                    jsonPath("$.content[0].id") { value(1L) }
                    jsonPath("$.content[0].name") { value("김테스트강사") }
                }
            }
        }

        When("검색 결과가 없으면") {
            val emptyPage = PageImpl(emptyList<FindInstructorResult>(), pageable, 0)

            every { searchInstructorUseCase.search(searchCriteria, any()) } returns emptyPage

            Then("200 OK 상태 코드와 함께 빈 페이지 응답을 반환해야 한다") {
                mockMvc.get("/instructors") {
                    param("deletionStatus", "ACTIVE")
                    with(user(TestUserFixture.USER))
                }.andExpect {
                    status { isOk() }
                    jsonPath("$.content") { isEmpty() }
                    jsonPath("$.totalElements") { value(0) }
                }
            }
        }
    }

    Given("강사 삭제 API (/instructors/{instructorId})") {
        val instructorId = 1L

        When("ADMIN 권한을 가진 사용자가 삭제를 요청하면") {
            every { deleteInstructorUseCase.delete(instructorId) } just runs

            Then("204 No Content 상태 코드를 반환해야 한다") {
                mockMvc.delete("/instructors/$instructorId") {
                    with(user(TestUserFixture.ADMIN))
                    with(csrf())
                }.andExpect {
                    status { isNoContent() }
                }

                verify(exactly = 1) { deleteInstructorUseCase.delete(instructorId) }
            }
        }

        When("USER 권한을 가진 사용자가 삭제를 요청하면") {

            Then("403 Forbidden 에러가 발생해야 한다") {
                mockMvc.delete("/instructors/$instructorId") {
                    with(user(TestUserFixture.USER))
                    with(csrf())
                }.andExpect {
                    status { isForbidden() }
                }

                verify(exactly = 1) { deleteInstructorUseCase.delete(instructorId) }
            }
        }
    }

    Given("강사 정보 수정 API (/instructors/{instructorId}/info)") {
        val instructorId = 1L
        val request = UpdateInstructorInfoRequest(name = "수정된 이름", bio = "수정된 소개")
        val now = LocalDateTime.now()
        val expectedResult = FindInstructorResult(
            id = instructorId,
            name = request.name,
            bio = request.bio,
            profileImageUrl = null,
            createdAt = now,
            modifiedAt = now,
            deletedAt = null
        )

        When("ADMIN 권한을 가진 사용자가 수정을 요청하면") {
            every { updateInstructorInfoUseCase.updateInfo(request.toCommand(instructorId)) } returns expectedResult

            Then("200 OK 상태 코드와 함께 수정된 강사 정보를 반환해야 한다") {
                mockMvc.patch("/instructors/$instructorId/info") {
                    with(user(TestUserFixture.ADMIN))
                    with(csrf())
                    contentType = MediaType.APPLICATION_JSON
                    content = objectMapper.writeValueAsString(request)
                }.andExpect {
                    status { isOk() }
                    jsonPath("$.id") { value(instructorId) }
                    jsonPath("$.name") { value(request.name) }
                    jsonPath("$.bio") { value(request.bio) }
                }

                verify(exactly = 1) { updateInstructorInfoUseCase.updateInfo(request.toCommand(instructorId)) }
            }
        }

        When("USER 권한을 가진 사용자가 수정을 요청하면") {
            Then("403 Forbidden 에러가 발생해야 한다") {
                mockMvc.patch("/instructors/$instructorId/info") {
                    with(user(TestUserFixture.USER))
                    with(csrf())
                    contentType = MediaType.APPLICATION_JSON
                    content = objectMapper.writeValueAsString(request)
                }.andExpect {
                    status { isForbidden() }
                }
                verify(exactly = 1) { updateInstructorInfoUseCase.updateInfo(any()) }
            }
        }
    }

    Given("강사 프로필 이미지 수정 API (/instructors/{instructorId}/profile-image)") {
        val instructorId = 1L
        val request = FileMetadata(filename = "new-profile.png", contentType = "image/png")
        val now = LocalDateTime.now()
        val expectedResult = RegisterInstructorResult(
            instructorId = instructorId,
            name = "기존 이름",
            bio = "기존 소개",
            preSignedUrl = "https://new.presigned.url/...",
            fileKey = "new/file/key.png",
            createdAt = now
        )

        When("ADMIN 사용자가 프로필 이미지 수정을 요청하면") {
            val validatedMetadata = ValidatedFileMetadata(request.filename!!, request.contentType!!)
            every {
                updateInstructorProfileImageUrlUseCase
                    .updateProfileImageUrl(instructorId, validatedMetadata)
            } returns expectedResult

            Then("200 OK 상태 코드와 함께 pre-signed URL 정보를 반환해야 한다") {
                mockMvc.patch("/instructors/$instructorId/profile-image") {
                    with(user(TestUserFixture.ADMIN))
                    with(csrf())
                    contentType = MediaType.APPLICATION_JSON
                    content = objectMapper.writeValueAsString(request)
                }.andExpect {
                    status { isOk() }
                    jsonPath("$.instructorId") { value(instructorId) }
                    jsonPath("$.preSignedUrl") { value(expectedResult.preSignedUrl) }
                    jsonPath("$.fileKey") { value(expectedResult.fileKey) }
                }

                verify(exactly = 1) {
                    updateInstructorProfileImageUrlUseCase.updateProfileImageUrl(
                        instructorId,
                        validatedMetadata
                    )
                }
            }
        }
    }
})
