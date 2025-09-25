package com.soomsoom.backend.adapter.out.persistence.mission

import com.soomsoom.backend.adapter.out.persistence.mission.repository.jpa.MissionCompletionLogJpaRepository
import com.soomsoom.backend.application.port.out.mission.MissionCompletionLogPort
import com.soomsoom.backend.domain.mission.model.entity.MissionCompletionLog
import org.springframework.stereotype.Component
import java.time.LocalDateTime

@Component
class MissionCompletionLogPersistenceAdapter(
    private val missionCompletionLogJpaRepository: MissionCompletionLogJpaRepository,
) : MissionCompletionLogPort {
    override fun save(log: MissionCompletionLog): MissionCompletionLog {
        return missionCompletionLogJpaRepository.save(log.toEntity()).toDomain()
    }

    override fun existsByCompletedAtBetween(userId: Long, missionId: Long, from: LocalDateTime, to: LocalDateTime): Boolean {
        return missionCompletionLogJpaRepository.existsByUserIdAndMissionIdAndCompletedAtBetween(
            userId,
            missionId,
            from,
            to
        )
    }

    override fun findCompletedButUnrewardedLog(userId: Long, missionId: Long, from: LocalDateTime, to: LocalDateTime): MissionCompletionLog? {
        return missionCompletionLogJpaRepository.findByUserIdAndMissionIdAndCompletedAtBetweenAndRewardedAtIsNull(
            userId = userId,
            missionId = missionId,
            from = from,
            to = to
        )?.toDomain()
    }

    // 이 메서드는 FirstEverBreathingStrategy 등에서 필요합니다.
    override fun exists(userId: Long, missionId: Long): Boolean {
        return missionCompletionLogJpaRepository.existsByUserIdAndMissionId(userId, missionId)
    }

    override fun existsWithUnrewarded(userId: Long, missionId: Long): Boolean {
        return missionCompletionLogJpaRepository.existsByUserIdAndMissionIdAndRewardedAtIsNull(userId, missionId)
    }
}
