package com.soomsoom.backend.adapter.out.persistence.activityhistory.repository.jpa

import com.soomsoom.backend.adapter.out.persistence.activityhistory.repository.jpa.entity.ActivityCompletionLogJpaEntity
import org.springframework.data.jpa.repository.JpaRepository

interface ActivityCompletionLogJpaRepository : JpaRepository<ActivityCompletionLogJpaEntity, Long> {
    fun countByUserId(userId: Long): Long
}
