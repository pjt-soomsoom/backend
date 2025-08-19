package com.soomsoom.backend.application.service.activity.command

import com.soomsoom.backend.adapter.out.persistence.activity.repository.jpa.dto.ActivityWithInstructorsDto
import com.soomsoom.backend.adapter.out.persistence.activity.repository.jpa.entity.BreathingActivityJpaEntity
import com.soomsoom.backend.adapter.out.persistence.instructor.repository.jpa.entity.InstructorJpaEntity
import com.soomsoom.backend.application.port.`in`.activity.command.UpdateActivityTimelineCommand
import com.soomsoom.backend.application.port.out.activity.ActivityPort
import com.soomsoom.backend.common.exception.SoomSoomException
import com.soomsoom.backend.domain.activity.ActivityErrorCode
import com.soomsoom.backend.domain.activity.model.BreathingActivity
import com.soomsoom.backend.domain.activity.model.MeditationActivity
import com.soomsoom.backend.domain.activity.model.TimelineEvent
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.spyk
import io.mockk.verify
import java.time.LocalDateTime

class UpdateActivityTimelineServiceTest : BehaviorSpec({
    // Mock 객체 준비
    val activityPort = mockk<ActivityPort>()

    // 테스트 대상 서비스 생성
    val updateActivityTimelineService = UpdateActivityTimelineService(activityPort)

    afterEach { clearAllMocks() }

    Given("타임라인을 수정할 BreathingActivity가 존재할 때") {
        val activityId = 1L
        val newTimeline = listOf(TimelineEvent(null, 10.0, "start", "시작", 5.0))
        val command = UpdateActivityTimelineCommand(activityId, newTimeline)

        // spyk를 사용하여 도메인 객체의 메서드 호출을 추적
        val originalActivity = spyk(
            BreathingActivity(
                id = activityId,
                title = "호흡 명상",
                descriptions = listOf("설명"),
                authorId = 1L,
                narratorId = 2L,
                durationInSeconds = 120,
                thumbnailImageUrl = null,
                thumbnailFileKey = null,
                audioUrl = null,
                audioFileKey = null,
                timeline = emptyList() // 원본 타임라인
            )
        )

        // 최종 결과를 위한 DTO Mocking
        val mockActivityEntity = mockk<BreathingActivityJpaEntity>(relaxed = true) {
            every { createdAt } returns LocalDateTime.now()
            every { title } returns "호흡 명상"
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

        When("updateTimeline 메서드를 실행하면") {
            val result = updateActivityTimelineService.updateTimeline(command)

            Then("Activity를 찾아 타임라인을 수정한 후 저장하고, 수정된 결과를 반환해야 한다") {
                // 1. Activity를 ID로 조회했는지 검증
                verify(exactly = 1) { activityPort.findById(activityId) }

                // 2. 도메인 객체의 updateTimeline 메서드가 올바른 인자로 호출되었는지 검증
                verify(exactly = 1) { originalActivity.updateTimeline(newTimeline) }

                // 3. 수정된 Activity 객체를 저장했는지 검증
                verify(exactly = 1) { activityPort.save(originalActivity) }

                // 4. 최종 결과를 위해 강사 정보와 함께 다시 조회했는지 검증
                verify(exactly = 1) { activityPort.findByIdWithInstructors(activityId) }

                // 5. 반환된 결과의 내용이 올바른지 검증 (간단한 sanity check)
                result.title shouldBe "호흡 명상"
            }
        }
    }

    Given("타임라인을 수정하려는 Activity가 BreathingActivity가 아닐 때") {
        val activityId = 2L
        val command = UpdateActivityTimelineCommand(activityId, emptyList())

        // BreathingActivity가 아닌 MeditationActivity를 반환하도록 설정
        val meditationActivity = MeditationActivity(
            id = activityId,
            title = "일반 명상",
            descriptions = listOf("설명"),
            authorId = 1L,
            narratorId = 2L,
            durationInSeconds = 180,
            thumbnailImageUrl = null,
            thumbnailFileKey = null,
            audioUrl = null,
            audioFileKey = null
        )

        every { activityPort.findById(activityId) } returns meditationActivity

        When("updateTimeline 메서드를 실행하면") {
            Then("UNSUPPORTED_ACTIVITY_TYPE 예외가 발생해야 한다") {
                val exception = shouldThrow<SoomSoomException> {
                    updateActivityTimelineService.updateTimeline(command)
                }
                exception.errorCode shouldBe ActivityErrorCode.UNSUPPORTED_ACTIVITY_TYPE

                // 타입이 맞지 않으므로 save는 호출되지 않아야 함
                verify(exactly = 0) { activityPort.save(any()) }
            }
        }
    }

    Given("존재하지 않는 Activity ID로 타임라인 수정을 요청했을 때") {
        val nonExistentId = 99L
        val command = UpdateActivityTimelineCommand(nonExistentId, emptyList())

        // findById가 null을 반환하도록 설정
        every { activityPort.findById(nonExistentId) } returns null

        When("updateTimeline 메서드를 실행하면") {
            Then("NOT_FOUND 예외가 발생해야 한다") {
                val exception = shouldThrow<SoomSoomException> {
                    updateActivityTimelineService.updateTimeline(command)
                }
                exception.errorCode shouldBe ActivityErrorCode.NOT_FOUND

                // Activity를 찾지 못했으므로 save는 호출되지 않아야 함
                verify(exactly = 0) { activityPort.save(any()) }
            }
        }
    }
})
