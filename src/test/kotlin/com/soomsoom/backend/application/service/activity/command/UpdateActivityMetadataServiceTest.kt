package com.soomsoom.backend.application.service.activity.command

import com.soomsoom.backend.adapter.out.persistence.activity.repository.jpa.dto.ActivityWithInstructorsDto
import com.soomsoom.backend.adapter.out.persistence.activity.repository.jpa.entity.MeditationActivityJpaEntity
import com.soomsoom.backend.adapter.out.persistence.instructor.repository.jpa.entity.InstructorJpaEntity
import com.soomsoom.backend.application.port.`in`.activity.command.UpdateActivityMetadataCommand
import com.soomsoom.backend.application.port.out.activity.ActivityPort
import com.soomsoom.backend.common.exception.SoomSoomException
import com.soomsoom.backend.domain.activity.ActivityErrorCode
import com.soomsoom.backend.domain.activity.model.MeditationActivity
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.mockk.*
import java.time.LocalDateTime

class UpdateActivityMetadataServiceTest : BehaviorSpec({
    // Mock 객체 준비
    val activityPort = mockk<ActivityPort>()

    // 테스트 대상 서비스 생성
    val updateActivityMetadataService = UpdateActivityMetadataService(activityPort)

    afterEach { clearAllMocks() }

    Given("유효한 Activity 메타데이터 수정 정보가 주어졌을 때") {
        val activityId = 1L
        val command = UpdateActivityMetadataCommand(
            activityId = activityId,
            title = "수정된 제목",
            descriptions = listOf("수정된 설명1", "수정된 설명2")
        )

        // findById로 반환될 원본 도메인 객체 (spyk로 감싸서 메서드 호출 추적)
        val originalActivity = spyk(
            MeditationActivity(
                id = activityId,
                title = "원본 제목",
                descriptions = listOf("원본 설명"),
                authorId = 10L,
                narratorId = 20L,
                durationInSeconds = 300,
                thumbnailImageUrl = null,
                thumbnailFileKey = null,
                audioUrl = null,
                audioFileKey = null
            )
        )

        // findByIdWithInstructors로 반환될 DTO Mocking
        val mockActivityEntity = mockk<MeditationActivityJpaEntity>(relaxed = true) {
            every { createdAt } returns LocalDateTime.now()
            every { title } returns command.title // 수정된 제목을 반환하도록 설정
            every { descriptions } returns command.descriptions.toMutableList() // 수정된 설명을 반환하도록 설정
        }
        val mockAuthorEntity = mockk<InstructorJpaEntity>(relaxed = true) {
            every { createdAt } returns LocalDateTime.now()
        }
        val mockNarratorEntity = mockk<InstructorJpaEntity>(relaxed = true) {
            every { createdAt } returns LocalDateTime.now()
        }
        val mockDto = ActivityWithInstructorsDto(mockActivityEntity, mockAuthorEntity, mockNarratorEntity)

        // Port 메서드들의 행동 정의
        every { activityPort.findById(activityId) } returns originalActivity
        every { activityPort.save(any()) } returns originalActivity
        every { activityPort.findByIdWithInstructors(activityId) } returns mockDto

        When("updateMetadata 메서드를 실행하면") {
            val result = updateActivityMetadataService.updateMetadata(command)

            Then("Activity를 찾아 메타데이터를 수정한 후 저장하고, 수정된 결과를 반환해야 한다") {
                // 1. Activity를 ID로 조회했는지 검증
                verify(exactly = 1) { activityPort.findById(activityId) }

                // 2. 도메인 객체의 updateMetadata 메서드가 올바른 인자로 호출되었는지 검증
                verify(exactly = 1) { originalActivity.updateMetadata(command.title, command.descriptions) }

                // 3. 수정된 Activity 객체를 저장했는지 검증
                verify(exactly = 1) { activityPort.save(originalActivity) }

                // 4. 최종 결과를 위해 강사 정보와 함께 다시 조회했는지 검증
                verify(exactly = 1) { activityPort.findByIdWithInstructors(activityId) }

                // 5. 반환된 결과의 내용이 올바른지 검증
                result.title shouldBe command.title
                result.descriptions shouldBe command.descriptions
            }
        }
    }

    Given("존재하지 않는 Activity ID로 수정을 요청했을 때") {
        val nonExistentId = 99L
        val command = UpdateActivityMetadataCommand(
            activityId = nonExistentId,
            title = "수정될 수 없는 제목",
            descriptions = listOf("설명")
        )

        // Mocking: findById가 null을 반환하도록 설정
        every { activityPort.findById(nonExistentId) } returns null

        When("updateMetadata 메서드를 실행하면") {
            Then("SoomSoomException(NOT_FOUND) 예외가 발생해야 한다") {
                val exception = shouldThrow<SoomSoomException> {
                    updateActivityMetadataService.updateMetadata(command)
                }
                exception.errorCode shouldBe ActivityErrorCode.NOT_FOUND

                // Activity를 찾지 못했으므로 save나 추가 조회가 호출되지 않아야 함
                verify(exactly = 0) { activityPort.save(any()) }
                verify(exactly = 0) { activityPort.findByIdWithInstructors(any()) }
            }
        }
    }
})
