package com.soomsoom.backend.adapter.out.persistence.mission.repository.jpa.entity

import com.soomsoom.backend.common.entity.BaseTimeEntity
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.time.LocalDateTime

@Entity
@Table(name = "mission_completion_logs")
class MissionCompletionLogJpaEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
    val userId: Long,
    val missionId: Long,
    val completedAt: LocalDateTime,
    var rewardedAt: LocalDateTime? = null,
) : BaseTimeEntity()
