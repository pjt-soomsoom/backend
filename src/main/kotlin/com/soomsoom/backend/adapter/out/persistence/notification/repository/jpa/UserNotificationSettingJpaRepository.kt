package com.soomsoom.backend.adapter.out.persistence.notification.repository.jpa

import com.soomsoom.backend.adapter.out.persistence.notification.repository.jpa.entity.UserNotificationSettingJpaEntity
import org.springframework.data.jpa.repository.JpaRepository

interface UserNotificationSettingJpaRepository : JpaRepository<UserNotificationSettingJpaEntity, Long> {
    fun findByUserId(userId: Long): UserNotificationSettingJpaEntity?
}
