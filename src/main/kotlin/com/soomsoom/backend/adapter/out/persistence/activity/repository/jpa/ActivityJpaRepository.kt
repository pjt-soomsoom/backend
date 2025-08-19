package com.soomsoom.backend.adapter.out.persistence.activity.repository.jpa

import com.soomsoom.backend.adapter.out.persistence.activity.repository.jpa.entity.ActivityJpaEntity
import org.springframework.data.jpa.repository.JpaRepository

interface ActivityJpaRepository : JpaRepository<ActivityJpaEntity, Long> {
    fun findByIdAndDeletedAtIsNull(id: Long): ActivityJpaEntity?
    fun findByIdAndDeletedAtIsNotNull(id: Long): ActivityJpaEntity?
}
