package com.soomsoom.backend.adapter.out.persistence.mission.repository

import com.soomsoom.backend.application.port.out.mission.UserMissionProgressPort
import com.soomsoom.backend.domain.mission.model.entity.UserMissionProgress
import org.springframework.stereotype.Component

@Component
class UserMissionProgressPersistenceAdapter() : UserMissionProgressPort {
    override fun save(progress: UserMissionProgress): UserMissionProgress {
        TODO("Not yet implemented")
    }

    override fun findOrCreate(userId: Long, missionId: Long): UserMissionProgress {
        TODO("Not yet implemented")
    }
}
