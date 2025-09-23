package com.soomsoom.backend.adapter.out.persistence.achievement.repository.jpa.entity

import com.soomsoom.backend.domain.achievement.model.enums.ConditionType
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Index
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table

@Entity
@Table(
    name = "achievement_conditions",
    indexes = [
        Index(name = "idx_ac_achievement_id", columnList = "achievement_id"),
        Index(name = "idx_ac_type", columnList = "type")
    ]
)
class AchievementConditionJpaEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "achievement_id", nullable = false)
    var achievement: AchievementJpaEntity? = null,

    @Enumerated(EnumType.STRING)
    val type: ConditionType,

    @Column(name = "target_value")
    val targetValue: Int,
)
