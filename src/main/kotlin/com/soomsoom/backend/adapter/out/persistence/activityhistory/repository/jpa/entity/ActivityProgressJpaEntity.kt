package com.soomsoom.backend.adapter.out.persistence.activityhistory.repository.jpa.entity

import com.soomsoom.backend.common.entity.BaseTimeEntity
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import jakarta.persistence.UniqueConstraint

@Entity
@Table(
    name = "activity_progress",
    uniqueConstraints = [
        UniqueConstraint(name = "uk_ap_user_activity", columnNames = ["user_id", "activity_id"])
    ]
)
class ActivityProgressJpaEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
    val userId: Long,
    val activityId: Long,
    var progressSeconds: Int,
) : BaseTimeEntity()
