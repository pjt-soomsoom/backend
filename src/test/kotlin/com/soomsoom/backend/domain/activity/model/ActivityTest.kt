package com.soomsoom.backend.domain.activity.model

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import java.time.LocalDateTime

class ActivityTest : BehaviorSpec({
    // --- 테스트 데이터 준비 ---
    val baseActivity = MeditationActivity(
        id = 1L,
        title = "기본 명상",
        descriptions = listOf("기본 설명"),
        authorId = 10L,
        narratorId = 20L,
        durationInSeconds = 300,
        thumbnailImageUrl = "https://example.com/old_thumb.jpg",
        thumbnailFileKey = "old/thumb.jpg",
        audioUrl = "https://example.com/old_audio.mp3",
        audioFileKey = "old/audio.mp3",
        createdAt = LocalDateTime.now()
    )

    Given("Activity 공통 로직 테스트") {
        When("updateMetadata 메서드를 유효한 값으로 호출하면") {
            val activity = baseActivity
            val newTitle = "수정된 제목"
            val newDescriptions = listOf("수정된 설명1", "수정된 설명2")
            activity.updateMetadata(newTitle, newDescriptions)

            Then("제목과 설명이 성공적으로 변경되어야 한다") {
                activity.title shouldBe newTitle
                activity.descriptions shouldBe newDescriptions
            }
        }

        When("updateMetadata 메서드를 비어있는 제목으로 호출하면") {
            val activity = baseActivity
            Then("IllegalArgumentException 예외가 발생해야 한다") {
                shouldThrow<IllegalArgumentException> {
                    activity.updateMetadata("", listOf("설명"))
                }
            }
        }

        When("updateThumbnailImage 메서드를 호출하면") {
            val activity = baseActivity
            val newUrl = "https://example.com/new_thumb.jpg"
            val newKey = "new/thumb.jpg"
            val returnedOldKey = activity.updateThumbnailImage(newUrl, newKey)

            Then("썸네일 URL과 파일 키가 변경되고, 이전 파일 키를 반환해야 한다") {
                activity.thumbnailImageUrl shouldBe newUrl
                activity.thumbnailFileKey shouldBe newKey
                returnedOldKey shouldBe "old/thumb.jpg"
            }
        }

        When("updateAudio 메서드를 호출하면") {
            val activity = baseActivity
            val newUrl = "https://example.com/new_audio.mp3"
            val newKey = "new/audio.mp3"
            val returnedOldKey = activity.updateAudio(newUrl, newKey)

            Then("오디오 URL과 파일 키가 변경되고, 이전 파일 키를 반환해야 한다") {
                activity.audioUrl shouldBe newUrl
                activity.audioFileKey shouldBe newKey
                returnedOldKey shouldBe "old/audio.mp3"
            }
        }

        When("delete 메서드를 호출하면") {
            val activity = baseActivity
            activity.delete()

            Then("isDeleted는 true가 되고 deletedAt 필드에 값이 할당되어야 한다") {
                activity.isDeleted shouldBe true
                activity.deletedAt shouldNotBe null
            }
        }
    }

    Given("BreathingActivity 특정 로직 테스트") {
        val breathingActivity = BreathingActivity(
            id = 2L, title = "호흡 명상", descriptions = listOf("호흡 설명"),
            authorId = 10L, narratorId = 20L, durationInSeconds = 180,
            thumbnailImageUrl = null, thumbnailFileKey = null, audioUrl = null, audioFileKey = null,
            timeline = emptyList()
        )

        When("updateTimeline 메서드를 호출하면") {
            val newTimeline = listOf(TimelineEvent(null, 10.0, "start", "시작", 5.0))
            breathingActivity.updateTimeline(newTimeline)

            Then("타임라인 정보가 성공적으로 변경되어야 한다") {
                breathingActivity.timeline shouldBe newTimeline
            }
        }
    }
})
