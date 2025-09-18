package com.soomsoom.backend.domain.notification.model.entity

/**
 * A/B 테스트를 위한 개별 메시지 후보군(Variation) Entity.
 * @property id 고유 식별자. 성과 추적의 기준
 * @property templateType 이 메시지가 어떤 템플릿 그룹에 속하는지 명시. (NotificationTemplate의 type과 N:1 관계)
 * @property titleTemplate 알림의 제목 템플릿 (e.g., "새로운 업적 달성! 🎉")
 * @property bodyTemplate 알림의 본문 템플릿. String.format 형식의 파라미터를 가질 수 있음. (e.g., "'%s' 업적 달성!")
 * @property isActive 이 특정 메시지 후보군의 발송 활성화 여부.
 */
class MessageVariation(
    val id: Long = 0,
    val notificationTemplate: NotificationTemplate,
    var titleTemplate: String,
    var bodyTemplate: String,
    var isActive: Boolean,
) {
    fun update(title: String, body: String, isActive: Boolean) {
        this.titleTemplate = title
        this.bodyTemplate = body
        this.isActive = isActive
    }
}
