package com.soomsoom.backend.application.port.out.mission

import com.soomsoom.backend.domain.mission.model.entity.UserMissionProgress

interface UserMissionProgressPort {
    fun save(progress: UserMissionProgress): UserMissionProgress
    fun findOrCreate(userId: Long, missionId: Long): UserMissionProgress
}
