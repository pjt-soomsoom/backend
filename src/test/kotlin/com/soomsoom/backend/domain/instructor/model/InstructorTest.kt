package com.soomsoom.backend.domain.instructor.model

import com.soomsoom.backend.common.exception.SoomSoomException
import com.soomsoom.backend.domain.instructor.InstructorErrorCode
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe

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

             Then("SoomSoomException(InstructorErrorCode.INSTRUCTOR_NAME_CANNOT_BE_BLANK) 예외가 발생해야 한다") {
                 val exception = shouldThrow<SoomSoomException> {
                     instructor.updateInfo(newName, newBio)
                 }
                 exception.errorCode shouldBe InstructorErrorCode.INSTRUCTOR_NAME_CANNOT_BE_BLANK
             }
         }

         When("updateProfileImageUrl 메서드를 호출하면") {
             val newImageUrl = "https://new.image.url/profile.jpg"
             instructor.updateProfileImageUrl(newImageUrl)

             Then("프로필 이미지 URL이 성공적으로 변경되어야 한다") {
                 instructor.profileImageUrl shouldBe newImageUrl
             }
         }
     }
 })
