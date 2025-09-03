package com.soomsoom.backend.adapter.out.persistence.achievement.repository.jpa

import com.soomsoom.backend.adapter.out.persistence.achievement.repository.jpa.entity.AchievementConditionJpaEntity
import com.soomsoom.backend.domain.achievement.model.ConditionType
import org.springframework.data.jpa.repository.JpaRepository

interface AchievementConditionJpaRepository : JpaRepository<AchievementConditionJpaEntity, Long> {
    fun findByAchievementId(achievementId: Long): List<AchievementConditionJpaEntity>
    fun findByType(type: ConditionType): List<AchievementConditionJpaEntity>

    /**
     * 특정 achievementId에 해당하는 모든 condition을 한 번에 삭제하는 메서드
     */
    fun deleteAllByAchievementId(achievementId: Long)
}
