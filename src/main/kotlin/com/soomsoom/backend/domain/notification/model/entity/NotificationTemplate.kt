package com.soomsoom.backend.domain.notification.model.entity

import com.soomsoom.backend.domain.notification.model.enums.NotificationType

/**
 * 알림 메시지 템플릿들의 '그룹'을 정의하는 Entity.
 * 자신에게 속한 여러 개의 MessageVariation을 관리
 * @property id 고유 식별자 (PK)
 * @property type 이 템플릿 그룹이 어떤 종류의 알림인지 식별하는 ID.
 * @property description 관리자가 이 템플릿 그룹의 용도를 쉽게 파악하기 위한 설명
 * @property isActive 이 그룹에 속한 모든 알림 메시지의 발송을 전역적으로 활성화/비활성화
 * @property triggerCondition 이 템플릿 그룹이 적용될 트리거의 구체적인 조건 값.
 * @property variations 이 템플릿 그룹에 속한 메시지 후보군(Variation) 목록
 */

class NotificationTemplate(
    val id: Long = 0L,
    var type: NotificationType,
    var description: String,
    var isActive: Boolean,
    var triggerCondition: Int? = null,
    variations: MutableList<MessageVariation> = mutableListOf(),
) {
    private val _variations: MutableList<MessageVariation> = variations
    val variations: List<MessageVariation> get() = _variations.toList()

    fun update(type: NotificationType, description: String, isActive: Boolean, triggerCondition: Int?) {
        this.type = type
        this.description = description
        this.isActive = isActive
        this.triggerCondition = triggerCondition
    }

    /**
     * 이 템플릿 그룹에 새로운 메시지 후보군을 추가
     */
    fun addVariation(title: String, body: String) {
        val newVariation = MessageVariation(
            notificationTemplate = this,
            titleTemplate = title,
            bodyTemplate = body,
            isActive = true
        )
        this._variations.add(newVariation)
    }

    /**
     * 이 템플릿 그룹에서 특정 메시지 후보군을 제거
     */
    fun removeVariation(variationId: Long) {
        this._variations.removeIf { it.id == variationId }
    }
}
