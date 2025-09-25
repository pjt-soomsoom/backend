package com.soomsoom.backend.adapter.out.persistence.mission.repository.jpa

import com.soomsoom.backend.adapter.out.persistence.mission.repository.jpa.entity.MissionCompletionLogJpaEntity
import org.springframework.data.jpa.repository.JpaRepository
import java.time.LocalDateTime

interface MissionCompletionLogJpaRepository : JpaRepository<MissionCompletionLogJpaEntity, Long> {
    fun existsByUserIdAndMissionIdAndCompletedAtBetween(
        userId: Long,
        missionId: Long,
        from: LocalDateTime,
        to: LocalDateTime,
    ): Boolean
    fun findByUserIdAndMissionIdAndCompletedAtBetweenAndRewardedAtIsNull(
        userId: Long,
        missionId: Long,
        from: LocalDateTime,
        to: LocalDateTime,
    ): MissionCompletionLogJpaEntity?
    fun existsByUserIdAndMissionId(userId: Long, missionId: Long): Boolean
    fun existsByUserIdAndMissionIdAndRewardedAtIsNull(userId: Long, missionId: Long): Boolean
}
