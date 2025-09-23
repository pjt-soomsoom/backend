package com.soomsoom.backend.adapter.out.persistence.notification.repository.jpa.entity

import com.soomsoom.backend.common.entity.BaseTimeEntity
import com.soomsoom.backend.domain.notification.model.enums.OSType
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Index
import jakarta.persistence.Table

@Entity
@Table(
    name = "user_devices",
    indexes = [
        // 특정 유저의 모든 디바이스를 빠르게 찾기 위한 인덱스
        Index(name = "idx_user_devices_user_id", columnList = "user_id")
    ]
)
class UserDeviceJpaEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(nullable = false)
    val userId: Long,

    @Column(unique = true, nullable = false, name = "fcm_token")
    val fcmToken: String,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, name = "os_type")
    val osType: OSType,
) : BaseTimeEntity()
