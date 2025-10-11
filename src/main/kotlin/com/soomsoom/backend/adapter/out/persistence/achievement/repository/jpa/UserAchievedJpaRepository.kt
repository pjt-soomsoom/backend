package com.soomsoom.backend.adapter.out.persistence.achievement.repository.jpa

import com.soomsoom.backend.adapter.out.persistence.achievement.repository.jpa.entity.UserAchievedJpaEntity
import org.springframework.data.jpa.repository.JpaRepository

interface UserAchievedJpaRepository : JpaRepository<UserAchievedJpaEntity, Long> {
    fun existsByUserIdAndAchievementId(userId: Long, achievementId: Long): Boolean
    fun deleteAllByUserId(userId: Long)
}
