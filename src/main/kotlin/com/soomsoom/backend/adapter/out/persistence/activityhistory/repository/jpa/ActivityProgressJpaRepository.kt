package com.soomsoom.backend.adapter.out.persistence.activityhistory.repository.jpa

import com.soomsoom.backend.adapter.out.persistence.activityhistory.repository.jpa.entity.ActivityProgressJpaEntity
import org.springframework.data.jpa.repository.JpaRepository

interface ActivityProgressJpaRepository : JpaRepository<ActivityProgressJpaEntity, Long> {
    fun findByUserIdAndActivityId(userId: Long, activityId: Long): ActivityProgressJpaEntity?
    fun deleteAllByUserId(userId: Long)
}
