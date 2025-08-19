package com.soomsoom.backend.adapter.out.persistence.activity.repository

import com.soomsoom.backend.IntegrationTest
import com.soomsoom.backend.adapter.out.persistence.activity.repository.jpa.ActivityJpaRepository
import com.soomsoom.backend.adapter.out.persistence.instructor.repository.jpa.InstructorJpaRepository
import com.soomsoom.backend.adapter.out.persistence.instructor.repository.jpa.entity.InstructorJpaEntity
import com.soomsoom.backend.application.port.`in`.activity.query.SearchActivitiesCriteria
import com.soomsoom.backend.domain.activity.model.BreathingActivity
import com.soomsoom.backend.domain.common.DeletionStatus
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.PageRequest
import org.springframework.transaction.annotation.Transactional

@IntegrationTest
@Transactional
class ActivityPersistenceAdapterTest(
    @Autowired private val activityPersistenceAdapter: ActivityPersistenceAdapter,
    @Autowired private val activityJpaRepository: ActivityJpaRepository,
    @Autowired private val instructorJpaRepository: InstructorJpaRepository,
) : BehaviorSpec({

    // 테스트에 사용할 강사 엔티티를 미리 저장합니다.
    lateinit var author: InstructorJpaEntity
    lateinit var narrator: InstructorJpaEntity

    beforeSpec {
        author = instructorJpaRepository.save(InstructorJpaEntity(name = "작가 강사", bio = null, profileImageUrl = null, profileImageFileKey = null))
        narrator = instructorJpaRepository.save(InstructorJpaEntity(name = "나레이터 강사", bio = null, profileImageUrl = null, profileImageFileKey = null))
    }

    Given("저장할 새로운 활동 도메인 객체가 주어졌을 때") {
        val newActivity = BreathingActivity(
            id = null,
            title = "새로운 호흡 활동",
            descriptions = mutableListOf("설명1"), // ✨ 수정됨
            authorId = author.id,
            narratorId = narrator.id,
            durationInSeconds = 120,
            thumbnailImageUrl = null, thumbnailFileKey = null,
            audioUrl = null, audioFileKey = null,
            timeline = mutableListOf() // ✨ 수정됨
        )

        When("save 메서드를 실행하면") {
            val savedActivity = activityPersistenceAdapter.save(newActivity)

            Then("ID가 부여된 객체가 반환되고, DB에서 조회 가능해야 한다") {
                savedActivity.id shouldNotBe null
                savedActivity.title shouldBe newActivity.title

                val foundActivity = activityPersistenceAdapter.findById(savedActivity.id!!)
                foundActivity?.id shouldBe savedActivity.id
                foundActivity?.title shouldBe savedActivity.title
            }
        }
    }

    Given("수정할 기존 활동 정보가 DB에 저장되어 있을 때") {
        val existingActivity = activityPersistenceAdapter.save(
            BreathingActivity(
                id = null, title = "원본 제목", descriptions = mutableListOf(), // ✨ 수정됨
                authorId = author.id, narratorId = narrator.id, durationInSeconds = 100,
                thumbnailImageUrl = null, thumbnailFileKey = null, audioUrl = null, audioFileKey = null, timeline = mutableListOf() // ✨ 수정됨
            )
        )

        When("ID가 포함된 도메인 객체로 save 메서드를 실행하면") {
            activityPersistenceAdapter.save(
                BreathingActivity(
                    id = existingActivity.id, title = "수정된 제목", descriptions = mutableListOf("수정된 설명"), // ✨ 수정됨
                    authorId = author.id, narratorId = narrator.id, durationInSeconds = 150,
                    thumbnailImageUrl = null, thumbnailFileKey = null, audioUrl = null, audioFileKey = null, timeline = mutableListOf() // ✨ 수정됨
                )
            )

            Then("DB의 정보가 성공적으로 수정되어야 한다") {
                val updatedActivity = activityPersistenceAdapter.findById(existingActivity.id!!)
                updatedActivity shouldNotBe null
                updatedActivity?.title shouldBe "수정된 제목"
                updatedActivity?.descriptions shouldBe listOf("수정된 설명")
            }
        }
    }

    Given("활성 상태와 삭제된 상태의 활동이 DB에 저장되어 있을 때") {
        // 테스트 데이터 준비
        val activeActivity = activityPersistenceAdapter.save(
            BreathingActivity(id = null, title = "활성 활동", descriptions = mutableListOf(), authorId = author.id, narratorId = narrator.id, durationInSeconds = 10, thumbnailImageUrl = null, thumbnailFileKey = null, audioUrl = null, audioFileKey = null, timeline = mutableListOf()) // ✨ 수정됨
        )
        val deletedActivity = activityPersistenceAdapter.save(
            BreathingActivity(id = null, title = "삭제된 활동", descriptions = mutableListOf(), authorId = author.id, narratorId = narrator.id, durationInSeconds = 10, thumbnailImageUrl = null, thumbnailFileKey = null, audioUrl = null, audioFileKey = null, timeline = mutableListOf()) // ✨ 수정됨
        ).apply { delete() }
        activityPersistenceAdapter.save(deletedActivity)

        When("findById를 ACTIVE 필터로 조회하면") {
            val result = activityPersistenceAdapter.findById(activeActivity.id!!)
            Then("활성 활동만 조회되어야 한다") {
                result shouldNotBe null
                result?.id shouldBe activeActivity.id
                result?.isDeleted shouldBe false
            }
        }

        When("findByIdWithInstructors를 DELETED 필터로 조회하면") {
            val result = activityPersistenceAdapter.findByIdWithInstructors(deletedActivity.id!!, DeletionStatus.DELETED)
            Then("삭제된 활동이 강사 정보와 함께 조회되어야 한다") {
                result shouldNotBe null
                result?.activity?.id shouldBe deletedActivity.id
                result?.activity?.deletedAt shouldNotBe null
                result?.author?.id shouldBe author.id
            }
        }

        When("search를 ACTIVE 필터로 조회하면") {
            val criteria = SearchActivitiesCriteria(deletionStatus = DeletionStatus.ACTIVE)
            val pageable = PageRequest.of(0, 10)
            val resultPage = activityPersistenceAdapter.search(criteria, pageable)

            Then("활성 활동만 포함된 페이지가 반환되어야 한다") {
                resultPage.totalElements shouldBe 1
                resultPage.content[0].activity.id shouldBe activeActivity.id
            }
        }
    }
})
