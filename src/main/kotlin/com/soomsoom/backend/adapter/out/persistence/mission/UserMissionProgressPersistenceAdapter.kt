package com.soomsoom.backend.adapter.out.persistence.mission

import com.fasterxml.jackson.databind.ObjectMapper
import com.soomsoom.backend.adapter.out.persistence.mission.repository.jpa.UserMissionProgressJpaRepository
import com.soomsoom.backend.application.port.out.mission.UserMissionProgressPort
import com.soomsoom.backend.domain.mission.model.entity.UserMissionProgress
import com.soomsoom.backend.domain.mission.model.vo.AttendanceStreakProgress
import org.springframework.stereotype.Component
import java.time.LocalDateTime

@Component
class UserMissionProgressPersistenceAdapter(
    private val userMissionProgressJpaRepository: UserMissionProgressJpaRepository,
    private val objectMapper: ObjectMapper,
) : UserMissionProgressPort {
    override fun save(progress: UserMissionProgress): UserMissionProgress {
        val entity = userMissionProgressJpaRepository.save(progress.toEntity(objectMapper))
        return entity.toDomain(objectMapper)
    }

    override fun findOrCreate(userId: Long, missionId: Long): UserMissionProgress {
        return userMissionProgressJpaRepository.findByUserIdAndMissionId(userId, missionId)
            ?.toDomain(objectMapper)
            ?: UserMissionProgress(
                userId = userId,
                missionId = missionId,
                progressData = AttendanceStreakProgress(0, null), // 기본값
                updatedAt = LocalDateTime.now()
            )
    }
}
