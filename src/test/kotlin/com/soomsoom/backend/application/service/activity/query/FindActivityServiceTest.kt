package com.soomsoom.backend.application.service.activity.query

import com.soomsoom.backend.IntegrationTest
import com.soomsoom.backend.adapter.out.persistence.activity.repository.jpa.ActivityJpaRepository
import com.soomsoom.backend.adapter.out.persistence.activity.repository.jpa.entity.BreathingActivityJpaEntity
import com.soomsoom.backend.adapter.out.persistence.instructor.repository.jpa.InstructorJpaRepository
import com.soomsoom.backend.adapter.out.persistence.instructor.repository.jpa.entity.InstructorJpaEntity
import com.soomsoom.backend.application.port.`in`.activity.query.SearchActivitiesCriteria
import com.soomsoom.backend.common.exception.SoomSoomException
import com.soomsoom.backend.domain.activity.ActivityErrorCode
import com.soomsoom.backend.domain.common.DeletionStatus
import com.soomsoom.backend.withTestUser
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.PageRequest
import org.springframework.security.access.AccessDeniedException
import org.springframework.transaction.annotation.Transactional

@IntegrationTest
@Transactional
class FindActivityServiceTest(
    @Autowired private val findActivityService: FindActivityService,
    @Autowired private val activityJpaRepository: ActivityJpaRepository,
    @Autowired private val instructorJpaRepository: InstructorJpaRepository,
) : BehaviorSpec({

    lateinit var author: InstructorJpaEntity
    lateinit var narrator: InstructorJpaEntity

    beforeSpec {
        author = instructorJpaRepository.save(InstructorJpaEntity(name = "작가 강사", bio = "소개1", profileImageUrl = null, profileImageFileKey = null))
        narrator = instructorJpaRepository.save(
            InstructorJpaEntity(name = "나레이터 강사", bio = "소개2", profileImageUrl = null, profileImageFileKey = null)
        )
    }

    Given("존재하는 활동이 데이터베이스에 저장되어 있을 때") {
        val savedActivity = activityJpaRepository.save(
            BreathingActivityJpaEntity(
                title = "테스트 호흡",
                descriptions = mutableListOf("설명"),
                authorId = author.id,
                narratorId = narrator.id,
                durationInSeconds = 120,
                thumbnailImageUrl = null,
                audioUrl = null,
                timeline = mutableListOf()
            )
        )

        When("USER 권한으로 findActivity 메서드를 실행하면") {
            val result = withTestUser("USER") {
                findActivityService.findActivity(savedActivity.id, DeletionStatus.ACTIVE)
            }

            Then("JOIN된 강사 정보와 함께 ActivityResult DTO를 반환해야 한다") {
                result.id shouldBe savedActivity.id
                result.title shouldBe "테스트 호흡"
                result.author.id shouldBe author.id
                result.author.name shouldBe "작가 강사"
            }
        }
    }

    Given("삭제된 활동이 데이터베이스에 저장되어 있을 때") {
        val savedActivity = activityJpaRepository.save(
            BreathingActivityJpaEntity(
                title = "삭제된 호흡",
                descriptions = mutableListOf(),
                authorId = author.id,
                narratorId = narrator.id,
                durationInSeconds = 1,
                thumbnailImageUrl = null,
                audioUrl = null,
                timeline = mutableListOf()
            )
        ).apply { delete() }
        activityJpaRepository.save(savedActivity)

        When("ADMIN 권한으로 DELETED 상태를 조회하면") {
            val result = withTestUser("ADMIN") {
                findActivityService.findActivity(savedActivity.id, DeletionStatus.DELETED)
            }

            Then("삭제된 활동 정보가 정상적으로 반환되어야 한다") {
                result.id shouldBe savedActivity.id
            }
        }

        When("USER 권한으로 DELETED 상태를 조회하면") {
            Then("AccessDeniedException 예외가 발생해야 한다") {
                withTestUser("USER") {
                    shouldThrow<AccessDeniedException> {
                        findActivityService.findActivity(savedActivity.id, DeletionStatus.DELETED)
                    }
                }
            }
        }
    }

    Given("존재하지 않는 Activity ID가 주어졌을 때") {
        val nonExistentId = 999L

        When("findActivity 메서드를 실행하면") {
            Then("SoomSoomException(NOT_FOUND) 예외가 발생해야 한다") {
                withTestUser("USER") {
                    val exception = shouldThrow<SoomSoomException> {
                        findActivityService.findActivity(nonExistentId, DeletionStatus.ACTIVE)
                    }
                    exception.errorCode shouldBe ActivityErrorCode.NOT_FOUND
                }
            }
        }
    }

    Given("여러 활동이 데이터베이스에 저장되어 있을 때") {
        activityJpaRepository.save(
            BreathingActivityJpaEntity(
                title = "활동1",
                descriptions = mutableListOf(),
                authorId = author.id,
                narratorId = narrator.id,
                durationInSeconds = 100,
                thumbnailImageUrl = null,
                audioUrl = null,
                timeline = mutableListOf()
            )
        )
        activityJpaRepository.save(
            BreathingActivityJpaEntity(
                title = "활동2",
                descriptions = mutableListOf(),
                authorId = author.id,
                narratorId = narrator.id,
                durationInSeconds = 200,
                thumbnailImageUrl = null,
                audioUrl = null,
                timeline = mutableListOf()
            )
        )

        val criteria = SearchActivitiesCriteria(deletionStatus = DeletionStatus.ACTIVE)
        val pageable = PageRequest.of(0, 10)

        When("search 메서드를 실행하면") {
            val resultPage = withTestUser("USER") {
                findActivityService.search(criteria, pageable)
            }

            Then("페이징된 결과를 Page<ActivityResult>로 변환하여 반환해야 한다") {
                resultPage.totalElements shouldBe 2
                resultPage.content.size shouldBe 2
                resultPage.content[0].title shouldBe "활동2"
            }
        }
    }
})
