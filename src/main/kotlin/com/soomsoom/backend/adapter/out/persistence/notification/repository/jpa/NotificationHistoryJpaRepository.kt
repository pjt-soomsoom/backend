package com.soomsoom.backend.adapter.out.persistence.notification.repository.jpa

import com.soomsoom.backend.adapter.out.persistence.notification.repository.jpa.entity.NotificationHistoryJpaEntity
import org.springframework.data.jpa.repository.JpaRepository

interface NotificationHistoryJpaRepository : JpaRepository<NotificationHistoryJpaEntity, Long> {
    fun deleteAllByUserId(userId: Long)
}
