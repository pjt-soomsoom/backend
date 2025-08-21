package com.soomsoom.backend.adapter.out.persistence.activityhistory.repository.jpa.entity

import org.springframework.data.jpa.repository.JpaRepository

interface UserActivitySummaryJpaRepository : JpaRepository<UserActivitySummaryJpaEntity, Long> {
    fun findByUserId(userId: Long): UserActivitySummaryJpaEntity?
}
