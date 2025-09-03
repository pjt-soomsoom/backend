package com.soomsoom.backend.adapter.out.persistence.achievement.repository.jpa.entity

import com.soomsoom.backend.common.entity.BaseTimeEntity
import com.soomsoom.backend.domain.achievement.model.Achievement
import com.soomsoom.backend.domain.achievement.model.AchievementCategory
import com.soomsoom.backend.domain.achievement.model.AchievementGrade
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table

@Entity
@Table(name = "achievements")
class AchievementJpaEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
    var name: String,
    var description: String,
    var phrase: String?,
    @Enumerated(EnumType.STRING)
    var grade: AchievementGrade,
    @Enumerated(EnumType.STRING)
    var category: AchievementCategory,
    var rewardPoints: Int?,
    var rewardItemId: Long?,
) : BaseTimeEntity() {
    fun update(achievement: Achievement) {
        this.name = achievement.name
        this.description = achievement.description
        this.phrase = achievement.phrase
        this.grade = achievement.grade
        this.category = achievement.category
        this.rewardPoints = achievement.rewardPoints
        this.rewardItemId = achievement.rewardItemId
        this.deletedAt = achievement.deletedAt
    }
}
