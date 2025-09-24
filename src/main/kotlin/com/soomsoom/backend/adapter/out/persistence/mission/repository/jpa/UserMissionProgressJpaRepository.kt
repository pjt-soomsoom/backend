package com.soomsoom.backend.adapter.out.persistence.mission.repository.jpa

import com.soomsoom.backend.adapter.out.persistence.mission.repository.jpa.entity.UserMissionProgressJpaEntity
import org.springframework.data.jpa.repository.JpaRepository

interface UserMissionProgressJpaRepository : JpaRepository<UserMissionProgressJpaEntity, Long> {
    fun findByUserIdAndMissionId(userId: Long, missionId: Long): UserMissionProgressJpaEntity?
}
