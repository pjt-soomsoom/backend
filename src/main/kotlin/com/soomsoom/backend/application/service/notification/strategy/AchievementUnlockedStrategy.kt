package com.soomsoom.backend.application.service.notification.strategy

import com.soomsoom.backend.application.port.out.notification.NotificationPort
import com.soomsoom.backend.application.port.out.notification.NotificationTemplatePort
import com.soomsoom.backend.common.event.Event
import com.soomsoom.backend.common.event.EventType
import com.soomsoom.backend.common.event.payload.AchievementAchievedNotificationPayload
import com.soomsoom.backend.domain.notification.model.enums.NotificationType
import com.soomsoom.backend.domain.notification.model.vo.NotificationMessage
import org.springframework.stereotype.Component

@Component
class AchievementUnlockedStrategy(
    private val notificationTemplatePort: NotificationTemplatePort,
    private val notificationPort: NotificationPort,
) : NotificationStrategy<AchievementAchievedNotificationPayload> {

    /**
     * 이 전략은 'ACHIEVEMENT_ACHIEVED' 타입의 이벤트만 처리하도록 명시
     */
    override fun supports(event: Event<*>) = event.eventType == EventType.ACHIEVEMENT_ACHIEVED

    /**
     * 업적 달성 이벤트를 기반으로 푸시 알림 메시지를 생성
     */
    override fun execute(event: Event<AchievementAchievedNotificationPayload>) {
        val payload = event.payload

        val activeTemplates = notificationTemplatePort.findActiveTemplatesWithActiveVariationsByType(NotificationType.ACHIEVEMENT_UNLOCKED)

        val variation = activeTemplates.firstOrNull()?.variations?.firstOrNull()
            ?: return

        val title = variation.titleTemplate
        val body = String.format(variation.bodyTemplate, payload.achievementName)

        val message = NotificationMessage(
            targetUserId = payload.userId,
            title = title,
            body = body,
            badgeCount = 0,
            payload = mapOf(
                "notificationType" to NotificationType.ACHIEVEMENT_UNLOCKED.name,
                "achievementId" to payload.achievementId.toString()
            )
        )

        notificationPort.send(message)
    }
}
