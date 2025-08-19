package com.soomsoom.backend.application.service.activity.command

import com.soomsoom.backend.application.port.out.activity.ActivityPort
import com.soomsoom.backend.common.exception.SoomSoomException
import com.soomsoom.backend.domain.activity.ActivityErrorCode
import com.soomsoom.backend.domain.activity.model.MeditationActivity
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify

class SoftDeleteActivityServiceTest : BehaviorSpec({
    // Mock 객체 준비
    val activityPort = mockk<ActivityPort>()

    // 테스트 대상 서비스 생성
    val softDeleteActivityService = SoftDeleteActivityService(activityPort)

    afterEach {
        clearAllMocks()
    }

    Given("존재하는 Activity ID가 주어졌을 때") {
        val activityId = 1L
        val activity = MeditationActivity(
            id = activityId,
            title = "삭제될 명상",
            descriptions = listOf("설명"),
            authorId = 1L,
            narratorId = 2L,
            durationInSeconds = 120,
            thumbnailImageUrl = null,
            thumbnailFileKey = null,
            audioUrl = null,
            audioFileKey = null
        )
        val activitySlot = slot<MeditationActivity>()

        // Mocking
        every { activityPort.findById(activityId) } returns activity
        every { activityPort.save(capture(activitySlot)) } returns activity

        When("softDeleteActivity 메서드를 실행하면") {
            softDeleteActivityService.softDeleteActivity(activityId)

            Then("Activity를 찾아 deletedAt을 설정하고 저장해야 한다") {
                // findById가 1번 호출되었는지 검증
                verify(exactly = 1) { activityPort.findById(activityId) }
                // save가 1번 호출되었는지 검증
                verify(exactly = 1) { activityPort.save(any()) }

                // save에 전달된 객체의 상태를 검증
                val capturedActivity = activitySlot.captured
                capturedActivity.isDeleted shouldBe true
                capturedActivity.deletedAt shouldNotBe null
            }
        }
    }

    Given("존재하지 않는 Activity ID가 주어졌을 때") {
        val nonExistentId = 99L

        // Mocking: findById가 null을 반환하도록 설정
        every { activityPort.findById(nonExistentId) } returns null

        When("softDeleteActivity 메서드를 실행하면") {
            Then("SoomSoomException(NOT_FOUND) 예외가 발생해야 한다") {
                val exception = shouldThrow<SoomSoomException> {
                    softDeleteActivityService.softDeleteActivity(nonExistentId)
                }
                exception.errorCode shouldBe ActivityErrorCode.NOT_FOUND

                // Activity를 찾지 못했으므로 save는 호출되지 않아야 함
                verify(exactly = 0) { activityPort.save(any()) }
            }
        }
    }
})
