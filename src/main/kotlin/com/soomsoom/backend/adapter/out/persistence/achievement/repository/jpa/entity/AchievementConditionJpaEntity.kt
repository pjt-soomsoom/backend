package com.soomsoom.backend.adapter.out.persistence.achievement.repository.jpa.entity

import com.soomsoom.backend.domain.achievement.model.ConditionType
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table

@Entity
@Table(name = "achievement_conditions")
class AchievementConditionJpaEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
    val achievementId: Long,
    @Enumerated(EnumType.STRING)
    val type: ConditionType,
    val targetValue: Int,
)
