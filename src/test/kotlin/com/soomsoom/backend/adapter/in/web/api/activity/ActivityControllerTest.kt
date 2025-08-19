package com.soomsoom.backend.adapter.`in`.web.api.activity

import com.fasterxml.jackson.databind.ObjectMapper
import com.ninjasquad.springmockk.MockkBean
import com.soomsoom.backend.adapter.`in`.security.config.SecurityConfig
import com.soomsoom.backend.adapter.`in`.security.provider.JwtTokenProvider
import com.soomsoom.backend.adapter.`in`.web.api.activity.config.ActivityConfigurer
import com.soomsoom.backend.adapter.`in`.web.api.activity.request.CreateActivityRequest
import com.soomsoom.backend.adapter.`in`.web.api.activity.request.update.UpdateActivityMetadataRequest
import com.soomsoom.backend.adapter.`in`.web.api.upload.request.FileMetadata
import com.soomsoom.backend.application.port.`in`.activity.dto.ActivityResult
import com.soomsoom.backend.application.port.`in`.activity.dto.CreateActivityResult
import com.soomsoom.backend.application.port.`in`.activity.usecase.command.ChangeActivityAudioUseCase
import com.soomsoom.backend.application.port.`in`.activity.usecase.command.ChangeActivityThumbnailUseCase
import com.soomsoom.backend.application.port.`in`.activity.usecase.command.CreateActivityUseCase
import com.soomsoom.backend.application.port.`in`.activity.usecase.command.SoftDeleteActivityUseCase
import com.soomsoom.backend.application.port.`in`.activity.usecase.command.UpdateActivityMetadataUseCase
import com.soomsoom.backend.application.port.`in`.activity.usecase.command.UpdateActivityTimelineUseCase
import com.soomsoom.backend.application.port.`in`.activity.usecase.query.FindActivityUseCase
import com.soomsoom.backend.application.port.`in`.activity.usecase.query.SearchActivitiesUseCase
import com.soomsoom.backend.domain.activity.model.ActivityType
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
import org.springframework.http.MediaType
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.delete
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.patch
import org.springframework.test.web.servlet.post

@WebMvcTest(controllers = [ActivityController::class])
@Import(SecurityConfig::class, ActivityConfigurer::class)
class ActivityControllerTest(
    @MockkBean private val createActivityUseCase: CreateActivityUseCase,
    @MockkBean private val findActivityUseCase: FindActivityUseCase,
    @MockkBean private val searchActivitiesUseCase: SearchActivitiesUseCase,
    @MockkBean private val updateActivityMetadataUseCase: UpdateActivityMetadataUseCase,
    @MockkBean private val updateActivityTimelineUseCase: UpdateActivityTimelineUseCase,
    @MockkBean private val changeActivityThumbnailUseCase: ChangeActivityThumbnailUseCase,
    @MockkBean private val changeActivityAudioUseCase: ChangeActivityAudioUseCase,
    @MockkBean private val softDeleteActivityUseCase: SoftDeleteActivityUseCase,
    @MockkBean private val jwtTokenProvider: JwtTokenProvider,
    private val mockMvc: MockMvc,
    private val objectMapper: ObjectMapper,
) : BehaviorSpec({

    Given("활동 생성 API (/activities)") {
        val request = CreateActivityRequest(
            title = "테스트 활동",
            descriptions = listOf("설명"),
            authorId = 1L,
            narratorId = 1L,
            durationInSeconds = 300,
            type = ActivityType.MEDITATION,
            thumbnailImageMetadata = FileMetadata("thumb.jpg", "image/jpeg"),
            audioMetadata = FileMetadata("audio.mp3", "audio/mpeg"),
            timeline = null
        )
        val expectedResult = CreateActivityResult(
            activityId = 1L,
            thumbnailUploadInfo = CreateActivityResult.FileUploadInfo("thumb-url", "thumb-key"),
            audioUploadInfo = CreateActivityResult.FileUploadInfo("audio-url", "audio-key")
        )

        When("ADMIN 권한으로 요청하면") {
            every { createActivityUseCase.create(any()) } returns expectedResult

            Then("201 Created와 함께 생성 결과를 반환한다") {
                mockMvc.post("/activities") {
                    with(user(TestUserFixture.ADMIN))
                    with(csrf())
                    contentType = MediaType.APPLICATION_JSON
                    content = objectMapper.writeValueAsString(request)
                }.andExpect {
                    status { isCreated() }
                    jsonPath("$.activityId") { value(1L) }
                    jsonPath("$.thumbnailUploadInfo.fileKey") { value("thumb-key") }
                }
            }
        }
    }

    Given("활동 삭제 API (/activities/{activityId})") {
        val activityId = 1L

        When("ADMIN 권한으로 요청하면") {
            every { softDeleteActivityUseCase.softDeleteActivity(activityId) } just runs

            Then("204 No Content를 반환한다") {
                mockMvc.delete("/activities/$activityId") {
                    with(user(TestUserFixture.ADMIN))
                    with(csrf())
                }.andExpect {
                    status { isNoContent() }
                }
                verify(exactly = 1) { softDeleteActivityUseCase.softDeleteActivity(activityId) }
            }
        }

        When("USER 권한으로 요청하면") {
            Then("403 Forbidden을 반환한다") {
                mockMvc.delete("/activities/$activityId") {
                    with(user(TestUserFixture.USER))
                    with(csrf())
                }.andExpect {
                    status { isForbidden() }
                }
            }
        }
    }

    Given("활동 메타데이터 수정 API (/activities/{activityId}/metadata)") {
        val activityId = 1L
        val request = UpdateActivityMetadataRequest("수정된 제목", listOf("수정된 설명"))
        val mockAuthor = ActivityResult.InstructorInfo(1L, "작가", null, null)
        val expectedResult = ActivityResult(activityId, request.title!!, null, request.descriptions!!, mockAuthor, mockAuthor, 300, null, null)

        When("ADMIN 권한으로 요청하면") {
            every { updateActivityMetadataUseCase.updateMetadata(any()) } returns expectedResult

            Then("200 OK와 함께 수정된 활동 정보를 반환한다") {
                mockMvc.patch("/activities/$activityId/metadata") {
                    with(user(TestUserFixture.ADMIN))
                    with(csrf())
                    contentType = MediaType.APPLICATION_JSON
                    content = objectMapper.writeValueAsString(request)
                }.andExpect {
                    status { isOk() }
                    jsonPath("$.title") { value("수정된 제목") }
                }
            }
        }
    }

    Given("활동 다 건 조회 API (/activities)") {
        val pageable = PageRequest.of(0, 10)
        val mockAuthor = ActivityResult.InstructorInfo(1L, "작가", null, null)
        val mockResult = ActivityResult(1L, "활동1", null, emptyList(), mockAuthor, mockAuthor, 120, null, null)
        val pagedResult = PageImpl(listOf(mockResult), pageable, 1)

        When("일반 사용자가 조회를 요청하면") {
            every { searchActivitiesUseCase.search(any(), any()) } returns pagedResult

            Then("200 OK와 함께 페이징된 결과를 반환한다") {
                mockMvc.get("/activities") {
                    with(user(TestUserFixture.USER))
                    param("page", "0")
                    param("size", "10")
                }.andExpect {
                    status { isOk() }
                    jsonPath("$.content.length()") { value(1) }
                    jsonPath("$.totalElements") { value(1) }
                    jsonPath("$.content[0].title") { value("활동1") }
                }
            }
        }
    }
})
