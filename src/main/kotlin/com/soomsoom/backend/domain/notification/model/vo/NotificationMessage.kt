package com.soomsoom.backend.domain.notification.model.vo

/**
 * 발송될 푸시 알림의 내용을 담는 불변 데이터 객체 (Value Object)
 * @property targetUserId 알림을 수신할 사용자의 ID
 * @property title 알림의 제목
 * @property body 알림의 본문 내용
 * @property imageUrl 알림에 포함될 이미지의 URL (선택 사항)
 * @property badgeCount 알림 수신 후 앱 아이콘에 표시될 숫자 (iOS 전용)
 * @property payload 클라이언트가 알림을 받고 특정 동작을 수행하는 데 필요한 데이터 맵.
 * (e.g., mapOf("notificationType" to "ACHIEVEMENT_UNLOCKED", "historyId" to "12345"))
 */
data class NotificationMessage(
    val targetUserId: Long,
    val title: String?,
    val body: String?,
    val imageUrl: String? = null,
    val badgeCount: Int,
    val payload: Map<String, String>,
)
