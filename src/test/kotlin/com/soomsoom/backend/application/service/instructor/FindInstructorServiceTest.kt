package com.soomsoom.backend.application.service.instructor

import com.soomsoom.backend.application.port.`in`.instructor.query.SearchInstructorsCriteria
import com.soomsoom.backend.application.port.out.instructor.InstructorPort
import com.soomsoom.backend.application.service.instructor.query.FindInstructorService
import com.soomsoom.backend.common.exception.SoomSoomException
import com.soomsoom.backend.domain.common.DeletionStatus
import com.soomsoom.backend.domain.instructor.InstructorErrorCode
import com.soomsoom.backend.domain.instructor.model.Instructor
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import java.time.LocalDateTime

class FindInstructorServiceTest : BehaviorSpec({
    val instructorPort = mockk<InstructorPort>()

    val findInstructorService = FindInstructorService(instructorPort)

    afterEach {
        clearAllMocks()
    }

    Given("존재하는 강사 ID와 DeletionStatus.ACTIVE가 주어졌을 때") {
        val instructorId = 1L
        val deletionStatus = DeletionStatus.DELETED
        val now = LocalDateTime.now()
        val mockInstructor = Instructor(
            id = instructorId,
            name = "박상훈",
            bio = "초보 명상가",
            createdAt = now
        )

        every { instructorPort.findById(instructorId, deletionStatus) } returns mockInstructor

        When("findById 메서드를 실행하면") {
            val result = findInstructorService.findById(instructorId, deletionStatus)

            Then("InstructorPort를 호출하고, FindInstructorResult DTO를 반환해야 한다.") {
                verify(exactly = 1) { instructorPort.findById(instructorId, deletionStatus) }
            }
        }
    }

    Given("존재하지 않는 강사 ID가 주어졌을 때") {
        val instructorId = 99L
        val filterStatus = DeletionStatus.ACTIVE

        // instructorPort.findById가 호출되면 null을 반환하도록 설정
        every { instructorPort.findById(instructorId, filterStatus) } returns null

        When("findById 메서드를 실행하면") {
            Then("SoomSoomException(INSTRUCTOR_NOT_FOUND) 예외가 발생해야 한다.") {
                val exception = shouldThrow<SoomSoomException> {
                    findInstructorService.findById(instructorId, filterStatus)
                }
                exception.errorCode shouldBe InstructorErrorCode.NOT_FOUND
            }
        }
    }

    Given("강사 목록 조회 조건이 주어졌을 때") {
        val criteria = SearchInstructorsCriteria(deletionStatus = DeletionStatus.ACTIVE)
        val pageable = PageRequest.of(0, 10)
        val now = LocalDateTime.now()
        val mockInstructor = Instructor(1L, "김강사", "안녕하세요", "url", profileImageFileKey = "key", now, now, null)
        val mockPage = PageImpl(listOf(mockInstructor), pageable, 1)

        // instructorPort.search가 호출되면 mockPage를 반환하도록 설정
        every { instructorPort.search(criteria, pageable) } returns mockPage

        When("search 메서드를 실행하면") {
            val resultPage = findInstructorService.search(criteria, pageable)

            Then("InstructorPort를 호출하고, 결과를 Page<FindInstructorResult>로 변환하여 반환해야 한다.") {
                verify(exactly = 1) { instructorPort.search(criteria, pageable) }

                resultPage.totalElements shouldBe 1
                resultPage.content.size shouldBe 1
                resultPage.content[0].name shouldBe "김강사"
            }
        }
    }
})
