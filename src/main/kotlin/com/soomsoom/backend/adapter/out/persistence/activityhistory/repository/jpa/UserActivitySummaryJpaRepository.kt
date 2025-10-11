package com.soomsoom.backend.adapter.out.persistence.activityhistory.repository.jpa

import com.soomsoom.backend.adapter.out.persistence.activityhistory.repository.jpa.entity.UserActivitySummaryJpaEntity
import org.springframework.data.jpa.repository.JpaRepository

interface UserActivitySummaryJpaRepository : JpaRepository<UserActivitySummaryJpaEntity, Long> {
    fun findByUserId(userId: Long): UserActivitySummaryJpaEntity?
    fun deleteAllByUserId(userId: Long)
}
