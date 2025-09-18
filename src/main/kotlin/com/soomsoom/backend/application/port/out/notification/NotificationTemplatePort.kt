package com.soomsoom.backend.application.port.out.notification

import com.soomsoom.backend.domain.notification.model.entity.MessageVariation
import com.soomsoom.backend.domain.notification.model.entity.NotificationTemplate
import com.soomsoom.backend.domain.notification.model.enums.NotificationType

interface NotificationTemplatePort {
    // ======== Template (알림 그룹) Port ========

    /** [Admin] 새로운 템플릿 그룹을 저장하거나 기존 템플릿을 수정 */
    fun saveTemplate(template: NotificationTemplate): NotificationTemplate

    /** [Admin] ID를 통해 템플릿 그룹을 삭제 */
    fun deleteTemplateById(id: Long)

    /** [Admin] ID를 통해 템플릿 그룹과 하위 Variation들을 함께 조회. (Fetch Join) */
    fun findTemplateByIdWithVariations(id: Long): NotificationTemplate?

    /** [Admin] 모든 템플릿 그룹 목록을 조회 */
    fun findAllTemplates(): List<NotificationTemplate>

    // ======== Message Variation (개별 메시지) Port ========

    /** [Admin] ID를 통해 메시지 후보군을 조회. */
    fun findVariationById(id: Long): MessageVariation?

    /** [Admin] ID를 통해 메시지 후보군을 조회. */
    fun saveVariation(variation: MessageVariation): MessageVariation

    // ======== System (Strategy용) Port ========

    /**
     * [System] 특정 타입에 해당하는 '활성화된' 템플릿 목록을 '활성화된' Variation들과 함께 조회. (Fetch Join)
     * Strategy에서 알림을 보낼 메시지 후보군을 찾기 위해 사용.
     * @param type 조회할 알림의 종류
     * @return 활성화된 Template 리스트 (활성화된 Variation 포함)
     */
    fun findActiveTemplatesWithActiveVariationsByType(type: NotificationType): List<NotificationTemplate>
}
