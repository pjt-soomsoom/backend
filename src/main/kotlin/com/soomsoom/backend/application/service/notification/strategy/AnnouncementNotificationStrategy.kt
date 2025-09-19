package com.soomsoom.backend.application.service.notification.strategy

import com.soomsoom.backend.application.port.out.notification.NotificationPort
import com.soomsoom.backend.application.port.out.notification.NotificationTemplatePort
import com.soomsoom.backend.application.port.out.notification.UserNotificationPort
import com.soomsoom.backend.common.event.Event
import com.soomsoom.backend.common.event.EventType
import com.soomsoom.backend.common.event.payload.AnnouncementCreatedNotificationPayload
import com.soomsoom.backend.domain.notification.model.enums.NotificationType
import com.soomsoom.backend.domain.notification.model.vo.NotificationMessage
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Component

@Component
class AnnouncementNotificationStrategy(
    private val notificationTemplatePort: NotificationTemplatePort,
    private val notificationPort: NotificationPort,
    private val userNotificationPort: UserNotificationPort,
    @Value("\${alarm.batch-size}")
    private val BATCH_SIZE: Int,
) : NotificationStrategy<AnnouncementCreatedNotificationPayload> {

    private val log = LoggerFactory.getLogger(javaClass)
    override fun supports(event: Event<*>) = event.eventType == EventType.ANNOUNCEMENT_CREATED

    override fun execute(event: Event<AnnouncementCreatedNotificationPayload>) {
        val payload = event.payload
        log.info("새로운 공지({}) 푸시 알림 발송 전략을 실행합니다.", payload.announcementId)

        val variation = notificationTemplatePort.findActiveTemplatesWithActiveVariationsByType(NotificationType.NEWS_UPDATE)
            .firstOrNull()?.variations?.firstOrNull() ?: return

        var pageNumber = 0
        while (true) {
            val pageable = PageRequest.of(pageNumber, BATCH_SIZE)

            val userInfosInBatch = userNotificationPort.findUserNotificationPushInfos(pageable)
            if (userInfosInBatch.isEmpty()) break

            val messagesInBatch = userInfosInBatch.map { userInfo ->
                val isEnabled = userInfo.isNewsNotificationEnabled
                NotificationMessage(
                    targetUserId = userInfo.userId,
                    title = if (isEnabled) variation.titleTemplate else null,
                    body = if (isEnabled) payload.title else null,
                    badgeCount = userInfo.unreadAnnouncementCount,
                    payload = mapOf(
                        "notificationType" to NotificationType.NEWS_UPDATE.name,
                        "type" to if (isEnabled) "VISIBLE_ANNOUNCEMENT" else "SILENT_BADGE_UPDATE",
                        "announcementId" to payload.announcementId.toString()
                    )
                )
            }

            if (messagesInBatch.isNotEmpty()) {
                notificationPort.sendAll(messagesInBatch)
                log.info("{}명의 사용자(페이지: {})에게 공지 관련 푸시 알림 발송을 요청했습니다.", messagesInBatch.size, pageNumber)
            }

            pageNumber++
        }
        log.info("모든 사용자에 대한 공지({}) 푸시 알림 발송 작업이 완료되었습니다.", payload.announcementId)
    }
}
