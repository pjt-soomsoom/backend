package com.soomsoom.backend.adapter.out.persistence.achievement.repository.jpa.dto

import com.querydsl.core.annotations.QueryProjection
import com.soomsoom.backend.adapter.out.persistence.achievement.repository.jpa.entity.AchievementJpaEntity
import com.soomsoom.backend.adapter.out.persistence.achievement.repository.jpa.entity.UserAchievedJpaEntity
import com.soomsoom.backend.adapter.out.persistence.achievement.repository.jpa.entity.UserProgressJpaEntity

data class AchievementWithProgressDto @QueryProjection constructor(
    val achievement: AchievementJpaEntity,
    val userAchieved: UserAchievedJpaEntity?,
    val userProgress: UserProgressJpaEntity?,
    val targetValue: Int,
)
