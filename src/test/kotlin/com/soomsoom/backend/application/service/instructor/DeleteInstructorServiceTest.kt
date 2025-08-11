package com.soomsoom.backend.application.service.instructor

import com.soomsoom.backend.application.port.out.instructor.InstructorPort
import com.soomsoom.backend.common.exception.SoomSoomException
import com.soomsoom.backend.domain.instructor.InstructorErrorCode
import com.soomsoom.backend.domain.instructor.model.Instructor
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify

class DeleteInstructorServiceTest : BehaviorSpec({
    val instructorPort = mockk<InstructorPort>()

    val deleteInstructorService = DeleteInstructorService(instructorPort)

    afterEach {
        clearAllMocks()
    }

    Given("존재하는 강사 ID가 주어졌을 때") {
        val instructorId = 1L
        val instructor = Instructor(id = instructorId, name = "삭제될 강사", bio = "소개")
        val instructorSlot = slot<Instructor>()

        every { instructorPort.findById(instructorId) } returns instructor
        every { instructorPort.save(capture(instructorSlot)) } returns instructor

        When("delete 메서드를 실행하면") {
            deleteInstructorService.delete(instructorId)

            Then("강사를 찾아 deletedAt을 설정하고 저장해야 한다") {
                verify(exactly = 1) { instructorPort.findById(instructorId) }
                verify(exactly = 1) { instructorPort.save(any()) }

                val capturedInstructor = instructorSlot.captured
                capturedInstructor.isDeleted shouldBe true
                capturedInstructor.deletedAt shouldNotBe null
            }
        }
    }

    Given("존재하지 않는 강사 ID가 주어졌을 때") {
        val nonExistentInstructorId = 99L

        every { instructorPort.findById(nonExistentInstructorId) } returns null

        When("delete 메서드를 실행하면") {
            Then("SoomSoomException(NOT_FOUND) 예외가 발생해야 한다") {
                val exception = shouldThrow<SoomSoomException> {
                    deleteInstructorService.delete(nonExistentInstructorId)
                }
                exception.errorCode shouldBe InstructorErrorCode.NOT_FOUND

                verify(exactly = 0) { instructorPort.save(any()) }
            }
        }
    }
})
