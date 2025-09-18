package com.soomsoom.backend.domain.notification.model.entity

import com.soomsoom.backend.domain.notification.model.enums.OSType

/**
 * 사용자의 푸시 알림 수신 디바이스 정보를 관리하는 Entity
 * @property id 고유 식별자
 * @property userId 디바이스를 소유한 사용자의 ID
 * @property fcmToken Firebase Cloud Messaging(FCM)에서 발급한, 특정 디바이스를 식별하는 고유 토큰
 * @property osType 디바이스의 운영체제 (플랫폼별 푸시 페이로드 구성을 위해 필수)
 */
class UserDevice(
    val id: Long = 0,
    var userId: Long,
    val fcmToken: String,
    val osType: OSType,
) {
    fun changeOwner(newUserId: Long) {
        this.userId = newUserId
    }
}
