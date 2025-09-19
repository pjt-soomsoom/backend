package com.soomsoom.backend.adapter.out.persistence.achievement

import com.soomsoom.backend.adapter.out.persistence.achievement.repository.jpa.entity.AchievementConditionJpaEntity
import com.soomsoom.backend.adapter.out.persistence.achievement.repository.jpa.entity.AchievementJpaEntity
import com.soomsoom.backend.adapter.out.persistence.achievement.repository.jpa.entity.UserAchievedJpaEntity
import com.soomsoom.backend.adapter.out.persistence.achievement.repository.jpa.entity.UserProgressJpaEntity
import com.soomsoom.backend.adapter.out.persistence.achievement.repository.jpa.entity.embedded.AchievementRewardEmbeddable
import com.soomsoom.backend.adapter.out.persistence.achievement.repository.jpa.entity.embedded.DisplayInfoEmbeddable
import com.soomsoom.backend.domain.achievement.model.aggregate.Achievement
import com.soomsoom.backend.domain.achievement.model.entity.AchievementCondition
import com.soomsoom.backend.domain.achievement.model.entity.UserAchieved
import com.soomsoom.backend.domain.achievement.model.entity.UserProgress
import com.soomsoom.backend.domain.achievement.model.vo.AchievementReward
import com.soomsoom.backend.domain.achievement.model.vo.DisplayInfo

// ===================================
// Achievement Aggregate Mapper
// ===================================

/**
 * AchievementJpaEntity (Persistence Model) -> Achievement (Domain Model)
 */
fun AchievementJpaEntity.toDomain(): Achievement {
    val achievement = Achievement(
        id = this.id,
        name = this.name,
        phrase = this.phrase,
        grade = this.grade,
        category = this.category,
        unlockedDisplayInfo = this.unlockedDisplayInfo.toDomain(),
        reward = this.reward?.toDomain(),
        conditions = this.conditions.map { it.toDomain() }
    )
    achievement.deletedAt = this.deletedAt
    return achievement
}

/**
 * Achievement (Domain Model) -> AchievementJpaEntity (Persistence Model)
 */
fun Achievement.toEntity(): AchievementJpaEntity {
    val entity = AchievementJpaEntity(
        id = this.id,
        name = this.name,
        phrase = this.phrase,
        grade = this.grade,
        category = this.category,
        unlockedDisplayInfo = this.unlockedDisplayInfo.toEmbeddable(),
        reward = this.reward?.toEmbeddable()
    )
    this.conditions.forEach { domainCondition ->
        entity.addCondition(domainCondition.toEntity())
    }
    entity.deletedAt = this.deletedAt
    return entity
}

// ===================================
// Value Object <-> Embeddable Mappers
// ===================================

// --- `toDomain` Mappers ---

private fun DisplayInfoEmbeddable.toDomain(): DisplayInfo =
    DisplayInfo(this.titleTemplate, this.bodyTemplate)

private fun AchievementRewardEmbeddable.toDomain(): AchievementReward = AchievementReward(
    points = this.rewardPoints,
    itemId = this.rewardItemId,
    displayInfo = this.rewardDisplayInfo.toDomain()
)

// --- ✨ `toEmbeddable` Mappers ---

/**
 * DisplayInfo (Domain VO) -> DisplayInfoEmbeddable (Persistence Embeddable)
 */
internal fun DisplayInfo.toEmbeddable(): DisplayInfoEmbeddable =
    DisplayInfoEmbeddable(this.titleTemplate, this.bodyTemplate)

/**
 * AchievementReward (Domain VO) -> AchievementRewardEmbeddable (Persistence Embeddable)
 */
internal fun AchievementReward.toEmbeddable(): AchievementRewardEmbeddable = AchievementRewardEmbeddable(
    rewardPoints = this.points,
    rewardItemId = this.itemId,
    rewardDisplayInfo = this.displayInfo.toEmbeddable() // 재귀적으로 호출
)

// ===================================
// Other Entity Mappers
// ===================================

fun AchievementConditionJpaEntity.toDomain(): AchievementCondition = AchievementCondition(id, type, targetValue)
fun AchievementCondition.toEntity(): AchievementConditionJpaEntity = AchievementConditionJpaEntity(id = id, type = type, targetValue = targetValue)

fun UserAchievedJpaEntity.toDomain(): UserAchieved = UserAchieved(id, userId, achievementId, achievedAt)
fun UserAchieved.toEntity(): UserAchievedJpaEntity = UserAchievedJpaEntity(id ?: 0, userId, achievementId, achievedAt)

fun UserProgressJpaEntity.toDomain(): UserProgress = UserProgress(id, userId, type, currentValue)
fun UserProgress.toEntity(): UserProgressJpaEntity = UserProgressJpaEntity(id ?: 0, userId, type, currentValue)
