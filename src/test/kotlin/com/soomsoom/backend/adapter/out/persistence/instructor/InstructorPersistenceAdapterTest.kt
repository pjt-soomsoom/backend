package com.soomsoom.backend.adapter.out.persistence.instructor

import com.soomsoom.backend.IntegrationTest
import com.soomsoom.backend.adapter.`in`.web.api.common.DeletionStatus
import com.soomsoom.backend.adapter.`in`.web.api.instructor.request.InstructorSearchCriteria
import com.soomsoom.backend.adapter.out.persistence.instructor.repository.jpa.InstructorJpaRepository
import com.soomsoom.backend.adapter.out.persistence.instructor.repository.jpa.InstructorQueryDslRepository
import com.soomsoom.backend.adapter.out.persistence.instructor.repository.jpa.entity.InstructorJpaEntity
import com.soomsoom.backend.domain.instructor.model.Instructor
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.PageRequest
import org.springframework.transaction.annotation.Transactional

@IntegrationTest
@Transactional
class InstructorPersistenceAdapterTest(
    @Autowired private val instructorPersistenceAdapter: InstructorPersistenceAdapter,
    @Autowired private val instructorJpaRepository: InstructorJpaRepository,
    @Autowired private val instructorQueryDslRepository: InstructorQueryDslRepository,
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

    Given("활성 상태와 삭제된 상태의 강사가 DB에 저장되어 있을 때") {
        // 테스트 데이터 준비
        val activeInstructor = instructorJpaRepository.save(
            InstructorJpaEntity(name = "활성 강사", bio = "소개1", profileImageUrl = null, profileImageFileKey = null)
        )
        val deletedInstructorEntity = instructorJpaRepository.save(
            InstructorJpaEntity(name = "삭제된 강사", bio = "소개2", profileImageUrl = null, profileImageFileKey = null)
        ).apply { delete() } // soft delete 처리
        instructorJpaRepository.save(deletedInstructorEntity)

        When("findById를 ACTIVE 필터로 조회하면") {
            val result = instructorPersistenceAdapter.findById(activeInstructor.id, DeletionStatus.ACTIVE)
            Then("활성 강사만 조회되어야 한다") {
                result shouldNotBe null
                result?.id shouldBe activeInstructor.id
                result?.isDeleted shouldBe false
            }
        }

        When("findById를 DELETED 필터로 조회하면") {
            val result = instructorPersistenceAdapter.findById(deletedInstructorEntity.id, DeletionStatus.DELETED)
            Then("삭제된 강사만 조회되어야 한다") {
                result shouldNotBe null
                result?.id shouldBe deletedInstructorEntity.id
                result?.isDeleted shouldBe true
            }
        }

        When("findById를 ALL 필터로 조회하면") {
            val result = instructorPersistenceAdapter.findById(deletedInstructorEntity.id, DeletionStatus.ALL)
            Then("삭제된 강사도 조회되어야 한다") {
                result shouldNotBe null
                result?.id shouldBe deletedInstructorEntity.id
            }
        }

        When("search를 ACTIVE 필터로 조회하면") {
            val criteria = InstructorSearchCriteria(deletionStatus = DeletionStatus.ACTIVE)
            val pageable = PageRequest.of(0, 10)
            val resultPage = instructorPersistenceAdapter.search(criteria, pageable)

            Then("활성 강사만 포함된 페이지가 반환되어야 한다") {
                resultPage.totalElements shouldBe 1
                resultPage.content[0].id shouldBe activeInstructor.id
            }
        }
    }
})
