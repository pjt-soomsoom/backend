package com.soomsoom.backend.adapter.out.persistence.achievement.repository.jpa.entity

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
    val name: String,
    val description: String,
    val phrase: String?,
    @Enumerated(EnumType.STRING)
    val grade: AchievementGrade,
    @Enumerated(EnumType.STRING)
    val category: AchievementCategory,
    val rewardPoints: Int?,
    val rewardItemId: Long?,
)
