package com.soomsoom.backend.adapter.out.persistence.mission.repository.jpa.entity

import com.soomsoom.backend.common.entity.BaseTimeEntity
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Index
import jakarta.persistence.Table
import java.time.LocalDateTime

@Entity
@Table(
    name = "mission_completion_logs",
    indexes = [Index(name = "idx_mission_log_user_mission_completed", columnList = "user_id, mission_id, completed_at")]
)
class MissionCompletionLogJpaEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(name = "user_id", nullable = false)
    val userId: Long,

    @Column(name = "mission_id", nullable = false)
    val missionId: Long,

    @Column(name = "completed_at", nullable = false)
    val completedAt: LocalDateTime,

    @Column(name = "rewarded_at")
    var rewardedAt: LocalDateTime? = null,
) : BaseTimeEntity()
