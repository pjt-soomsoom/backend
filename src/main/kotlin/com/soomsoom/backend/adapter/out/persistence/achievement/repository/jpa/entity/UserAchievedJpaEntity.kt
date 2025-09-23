package com.soomsoom.backend.adapter.out.persistence.achievement.repository.jpa.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import jakarta.persistence.UniqueConstraint
import java.time.LocalDateTime

@Entity
@Table(
    name = "user_achieved",
    uniqueConstraints = [
        UniqueConstraint(name = "uk_user_achieved_user_achievement", columnNames = ["user_id", "achievement_id"])
    ]
)
class UserAchievedJpaEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
    val userId: Long,

    @Column(name = "achievement_id")
    val achievementId: Long,

    @Column(name = "achieved_at")
    val achievedAt: LocalDateTime,
)
