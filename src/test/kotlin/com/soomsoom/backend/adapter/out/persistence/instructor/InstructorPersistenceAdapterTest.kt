package com.soomsoom.backend.adapter.out.persistence.instructor

import com.soomsoom.backend.IntegrationTest
import com.soomsoom.backend.domain.instructor.model.Instructor
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

@IntegrationTest
@Transactional
class InstructorPersistenceAdapterTest(
    @Autowired private val instructorPersistenceAdapter: InstructorPersistenceAdapter,
) : BehaviorSpec({

    Given("저장할 새로운 강사 도메인 객체가 주어졌을 때") {
        val newInstructor = Instructor(name = "테스트 강사", bio = "안녕")

        When("save 메서드를 실행하면") {
            val savedInstructor = instructorPersistenceAdapter.save(newInstructor)

            Then("ID가 부여된 객체가 반환되고, DB에서 조회 가능해야 한다") {
                savedInstructor.id shouldNotBe null
                savedInstructor.name shouldBe newInstructor.name

                val foundInstructor = instructorPersistenceAdapter.findById(savedInstructor.id!!)
                foundInstructor?.id shouldBe savedInstructor.id
                foundInstructor?.name shouldBe savedInstructor.name
            }
        }
    }

    Given("수정할 기존 강사 정보가 DB에 저장되어 있을 때") {
        val existingInstructor = instructorPersistenceAdapter.save(
            Instructor(name = "원본 이름", bio = "원본 소개")
        )

        When("ID가 포함된 도메인 객체로 save 메서드를 실행하면") {
            val save = instructorPersistenceAdapter.save(
                Instructor(existingInstructor.id, name = "수정된 이름", bio = "수정된 소개")
            )

            Then("DB의 정보가 성공적으로 수정되어야 한다") {
                val updatedInstructor = instructorPersistenceAdapter.findById(existingInstructor.id!!)
                updatedInstructor shouldNotBe null
                updatedInstructor?.name shouldBe "수정된 이름"
                updatedInstructor?.bio shouldBe "수정된 소개"
            }
        }
    }
})
