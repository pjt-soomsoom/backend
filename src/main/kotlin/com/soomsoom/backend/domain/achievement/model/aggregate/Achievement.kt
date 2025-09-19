package com.soomsoom.backend.domain.achievement.model.aggregate

import com.soomsoom.backend.domain.achievement.model.entity.AchievementCondition
import com.soomsoom.backend.domain.achievement.model.enums.AchievementCategory
import com.soomsoom.backend.domain.achievement.model.enums.AchievementGrade
import com.soomsoom.backend.domain.achievement.model.vo.AchievementReward
import com.soomsoom.backend.domain.achievement.model.vo.DisplayInfo
import java.time.LocalDateTime

/**
 * 업적(Achievement) Aggregate Root
 *
 * @property id 업적의 고유 식별자
 * @property name 업적의 이름 (예: "성장의 3일")
 * @property unlockedDisplayInfo 업적 달성 시 보여줄 메시지 정보
 * @property reward 업적에 대한 보상 정보 (보상이 없을 수도 있음)
 * @property phrase 업적의 문구
 * @property grade 업적 등급
 * @property category 업적 카테고리
 * @property conditions 이 업적을 달성하기 위한 조건 목록
 */
class Achievement(
    val id: Long,
    var name: String,
    var unlockedDisplayInfo: DisplayInfo,
    var reward: AchievementReward?,
    var phrase: String?,
    var grade: AchievementGrade,
    var category: AchievementCategory,
    var deletedAt: LocalDateTime? = null,
    var conditions: List<AchievementCondition> = emptyList(),
) {

    val isDeleted: Boolean
        get() = deletedAt != null

    val hasReward: Boolean
        get() = reward != null

    /**
     * 업적 정보를 업데이트합니다.
     */
    fun update(
        name: String,
        unlockedDisplayInfo: DisplayInfo,
        reward: AchievementReward?,
        phrase: String?,
        grade: AchievementGrade,
        category: AchievementCategory,
    ) {
        this.name = name
        this.unlockedDisplayInfo = unlockedDisplayInfo
        this.reward = reward
        this.phrase = phrase
        this.grade = grade
        this.category = category
    }

    /**
     * 업적을 삭제 처리합니다.
     */
    fun delete() {
        this.deletedAt = LocalDateTime.now()
    }

    companion object {
        /**
         * 새로운 업적을 생성합니다.
         */
        fun create(
            name: String,
            unlockedDisplayInfo: DisplayInfo,
            reward: AchievementReward?,
            phrase: String?,
            grade: AchievementGrade,
            category: AchievementCategory,
        ): Achievement {
            require(name.isNotBlank()) { "업적 이름은 비워둘 수 없습니다." }

            return Achievement(
                id = 0L,
                name = name,
                unlockedDisplayInfo = unlockedDisplayInfo,
                reward = reward,
                phrase = phrase,
                grade = grade,
                category = category
            )
        }
    }
}
