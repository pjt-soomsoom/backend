package com.soomsoom.backend.domain.instructor.model

import com.soomsoom.backend.common.exception.SoomSoomException
import com.soomsoom.backend.domain.instructor.InstructorErrorCode
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import java.time.LocalDateTime

class InstructorTest : BehaviorSpec({
    Given("새로운 강사 객체가 주어졌을 때") {
        val instructor = Instructor(
            1L,
            "박상훈",
            "초보 명상가"
        )

        When("updateInfo 메소드를 유효한 값으로 호출하면") {
            val newName = "박상훈2"
            val newBio = "경력 명상가."

            instructor.updateInfo(newName, newBio)

            Then("강사의 이름과 소개가 성공적으로 변경되어야 한다") {
                instructor.name shouldBe newName
                instructor.bio shouldBe newBio
            }
        }

        When("updateInfo 메서드를 비어있는 이름으로 호출하면") {
            val newName = ""
            val newBio = "소개만 변경"

            Then("SoomSoomException(InstructorErrorCode.NAME_CANNOT_BE_BLANK) 예외가 발생해야 한다") {
                val exception = shouldThrow<SoomSoomException> {
                    instructor.updateInfo(newName, newBio)
                }
                exception.errorCode shouldBe InstructorErrorCode.NAME_CANNOT_BE_BLANK
            }
        }

        When("updateProfileImageUrl 메서드를 호출하면") {
            val newImageUrl = "https://new.image.url/profile.jpg"
            val newFileKey = "new/profile.jpg"

            val oldFileKey = instructor.profileImageFileKey

            val returnedOldKey = instructor.updateProfileImageUrl(newImageUrl, newFileKey)

            Then("URL과 파일 키가 변경되고, 이전 파일 키(null)를 반환해야 한다") {
                instructor.profileImageUrl shouldBe newImageUrl
                instructor.profileImageFileKey shouldBe newFileKey
                returnedOldKey shouldBe oldFileKey // oldFileKey는 null이어야 합니다.
            }
        }
    }

    Given("강사 도메인 객체가 주어졌을 때") {
        val now = LocalDateTime.now()

        When("deletedAt이 null이면") {
            val instructor = Instructor(
                id = 1L,
                name = "활성 강사",
                bio = "소개",
                createdAt = now,
                modifiedAt = now,
                deletedAt = null
            )
            Then("isDeleted는 false여야 한다") {
                instructor.isDeleted shouldBe false
            }
        }

        When("deletedAt에 값이 있으면") {
            val instructor = Instructor(
                id = 1L,
                name = "삭제된 강사",
                bio = "소개",
                createdAt = now,
                modifiedAt = now,
                deletedAt = now
            )
            Then("isDeleted는 true여야 한다") {
                instructor.isDeleted shouldBe true
            }
        }
    }

    Given("삭제되지 않은 강사 객체가 주어졌을 때") {
        val instructor = Instructor(
            id = 1L,
            name = "삭제될 강사",
            bio = "이 강사는 곧 삭제됩니다."
        )

        When("delete 메서드를 호출하면") {
            instructor.delete()

            Then("isDeleted는 true가 되고 deletedAt 필드에 값이 할당되어야 한다") {
                instructor.isDeleted shouldBe true
                instructor.deletedAt shouldNotBe null
            }
        }
    }
})
