package com.soomsoom.backend.domain.notification.model.entity

import java.time.LocalDateTime

/**
 * 발송된 모든 푸시 알림의 기록을 저장하고 성과를 추적하는 Entity
 * @property id 고유 식별자. 이 ID를 통해 클릭 이벤트를 추적
 * @property userId 알림을 수신한 사용자 ID.
 * @property messageVariationId 발송된 메시지 후보군(Variation)의 ID. 어떤 문구가 보내졌는지 추적하기 위함
 * @property sentAt 알림이 발송된 시간.
 * @property clickedAt 사용자가 알림을 클릭한 시간. 클릭되지 않았으면 null
 */
class NotificationHistory(
    val id: Long = 0,
    val userId: Long,
    val messageVariationId: Long,
    val sentAt: LocalDateTime,
    var clickedAt: LocalDateTime? = null,
) {
    /**
     * 사용자가 알림을 클릭했음을 기록합니다.
     */
    fun recordClick() {
        if (this.clickedAt == null) { // 중복 클릭 방지
            this.clickedAt = LocalDateTime.now()
        }
    }
}
