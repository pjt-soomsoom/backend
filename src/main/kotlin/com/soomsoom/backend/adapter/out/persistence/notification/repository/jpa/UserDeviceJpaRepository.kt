package com.soomsoom.backend.adapter.out.persistence.notification.repository.jpa

import com.soomsoom.backend.adapter.out.persistence.notification.repository.jpa.entity.UserDeviceJpaEntity
import org.springframework.data.jpa.repository.JpaRepository

interface UserDeviceJpaRepository : JpaRepository<UserDeviceJpaEntity, Long> {
    fun findByFcmToken(fcmToken: String): UserDeviceJpaEntity?
    fun deleteByFcmToken(fcmToken: String)
    fun deleteAllByFcmTokenIn(fcmToken: List<String>)
}
