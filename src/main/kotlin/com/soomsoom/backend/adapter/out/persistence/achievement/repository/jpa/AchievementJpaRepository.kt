package com.soomsoom.backend.adapter.out.persistence.achievement.repository.jpa

import com.soomsoom.backend.adapter.out.persistence.achievement.repository.jpa.entity.AchievementJpaEntity
import org.springframework.data.jpa.repository.JpaRepository

interface AchievementJpaRepository : JpaRepository<AchievementJpaEntity, Long> {
    fun findByIdAndDeletedAtIsNull(id: Long): AchievementJpaEntity?
    fun findByIdAndDeletedAtIsNotNull(id: Long): AchievementJpaEntity?
}
