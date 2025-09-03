package com.soomsoom.backend.adapter.out.persistence.achievement

import com.soomsoom.backend.adapter.out.persistence.achievement.repository.jpa.entity.AchievementConditionJpaEntity
import com.soomsoom.backend.adapter.out.persistence.achievement.repository.jpa.entity.AchievementJpaEntity
import com.soomsoom.backend.adapter.out.persistence.achievement.repository.jpa.entity.UserAchievedJpaEntity
import com.soomsoom.backend.adapter.out.persistence.achievement.repository.jpa.entity.UserProgressJpaEntity
import com.soomsoom.backend.domain.achievement.model.Achievement
import com.soomsoom.backend.domain.achievement.model.AchievementCondition
import com.soomsoom.backend.domain.achievement.model.UserAchieved
import com.soomsoom.backend.domain.achievement.model.UserProgress

// Achievement
fun AchievementJpaEntity.toDomain(): Achievement = Achievement(id, name, description, phrase, grade, category, rewardPoints, rewardItemId, deletedAt)
fun Achievement.toEntity(): AchievementJpaEntity = AchievementJpaEntity(id, name, description, phrase, grade, category, rewardPoints, rewardItemId)
    .apply {
        this.deletedAt = this@toEntity.deletedAt
    }

// AchievementCondition
fun AchievementConditionJpaEntity.toDomain(): AchievementCondition = AchievementCondition(id, achievementId, type, targetValue)
fun AchievementCondition.toEntity(): AchievementConditionJpaEntity = AchievementConditionJpaEntity(id, achievementId, type, targetValue)

// UserAchieved
fun UserAchievedJpaEntity.toDomain(): UserAchieved = UserAchieved(id, userId, achievementId, achievedAt)
fun UserAchieved.toEntity(): UserAchievedJpaEntity = UserAchievedJpaEntity(id ?: 0, userId, achievementId, achievedAt)

// UserProgress
fun UserProgressJpaEntity.toDomain(): UserProgress = UserProgress(id, userId, type, currentValue)
fun UserProgress.toEntity(): UserProgressJpaEntity = UserProgressJpaEntity(id ?: 0, userId, type, currentValue)
