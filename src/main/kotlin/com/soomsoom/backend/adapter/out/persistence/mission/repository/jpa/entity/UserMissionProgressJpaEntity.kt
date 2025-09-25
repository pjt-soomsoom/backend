package com.soomsoom.backend.adapter.out.persistence.mission.repository.jpa.entity

import com.soomsoom.backend.common.entity.BaseTimeEntity
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Index
import jakarta.persistence.Table
import org.hibernate.annotations.JdbcTypeCode
import org.hibernate.type.SqlTypes

@Entity
@Table(
    name = "user_mission_progress",
    indexes = [Index(name = "uk_user_mission_progress_user_mission", columnList = "userId, missionId", unique = true)]
)
class UserMissionProgressJpaEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(name = "user_id", nullable = false)
    val userId: Long,

    @Column(name = "mission_id", nullable = false)
    val missionId: Long,

    @JdbcTypeCode(SqlTypes.JSON) // JSON 타입으로 DB에 저장
    @Column(columnDefinition = "json", nullable = false)
    var progressData: String,
) : BaseTimeEntity()
