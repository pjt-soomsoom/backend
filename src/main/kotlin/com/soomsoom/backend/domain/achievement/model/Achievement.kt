package com.soomsoom.backend.domain.achievement.model

import java.time.LocalDateTime

class Achievement(
    val id: Long,
    var name: String, // 업적 이름 (예: "마음 일기 첫 발걸음")
    var description: String, // 업적 설명 (예: "감정일기 1회 달성시")
    var phrase: String?,
    var grade: AchievementGrade,
    var category: AchievementCategory,
    var rewardPoints: Int?, // 보상 재화
    var rewardItemId: Long?, // 보상 아이템
    var deletedAt: LocalDateTime? = null,
) {

    lateinit var conditions: List<AchievementCondition>

    val isDeleted: Boolean
        get() = deletedAt != null

    fun update(
        name: String,
        description: String,
        phrase: String?,
        grade: AchievementGrade,
        category: AchievementCategory,
        rewardPoints: Int?,
        rewardItemId: Long?,
    ) {
        this.name = name
        this.description = description
        this.phrase = phrase
        this.grade = grade
        this.category = category
        this.rewardPoints = rewardPoints
        this.rewardItemId = rewardItemId
    }

    companion object {
        fun create(
            name: String,
            description: String,
            phrase: String?,
            grade: AchievementGrade,
            category: AchievementCategory,
            rewardPoints: Int?,
            rewardItemId: Long?,
        ): Achievement {
            require(name.isNotBlank()) { "업적 이름은 비워둘 수 없습니다." }
            require(description.isNotBlank()) { "업적 설명은 비워둘 수 없습니다." }

            return Achievement(
                id = 0L,
                name = name,
                description = description,
                phrase = phrase,
                grade = grade,
                category = category,
                rewardPoints = rewardPoints,
                rewardItemId = rewardItemId,
                deletedAt = null
            )
        }
    }

    fun delete() {
        this.deletedAt = LocalDateTime.now()
    }
}
