package com.soomsoom.backend.application.service.notification.strategy

import com.soomsoom.backend.application.port.out.notification.NotificationPort
import com.soomsoom.backend.application.port.out.notification.NotificationTemplatePort
import com.soomsoom.backend.application.port.out.notification.UserNotificationPort
import com.soomsoom.backend.common.event.Event
import com.soomsoom.backend.common.event.EventType
import com.soomsoom.backend.common.event.payload.SchedulerTickNotificationPayload
import com.soomsoom.backend.common.utils.DateHelper
import com.soomsoom.backend.domain.notification.model.entity.NotificationHistory
import com.soomsoom.backend.domain.notification.model.enums.NotificationType
import com.soomsoom.backend.domain.notification.model.vo.NotificationMessage
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.temporal.ChronoUnit

@Component
class DiaryReminderStrategy(
    private val notificationTemplatePort: NotificationTemplatePort,
    private val userNotificationPort: UserNotificationPort,
    private val notificationPort: NotificationPort,
    private val dateHelper: DateHelper,
    @Value("\${alarm.batch-size}")
    private val BATCH_SIZE: Int,
) : NotificationStrategy<SchedulerTickNotificationPayload> {

    private val log = LoggerFactory.getLogger(javaClass)

    override fun supports(event: Event<*>) = event.eventType == EventType.SCHEDULER_TICK

    @Transactional
    override fun execute(event: Event<SchedulerTickNotificationPayload>) {

        // 'DIARY_REMINDER' 타입의 활성화된 템플릿과 Variation들을 미리 조회합니다.
        val activeTemplates = notificationTemplatePort.findActiveTemplatesWithActiveVariationsByType(NotificationType.DIARY_REMINDER)
        val variations = activeTemplates.flatMap { it.variations }
        if (variations.isEmpty()) return

        val triggeredAt = event.payload.triggeredAt
        val currentTime = triggeredAt.toLocalTime().truncatedTo(ChronoUnit.MINUTES)
        var pageNumber = 0

        while (true) {
            // batch-size만큼 데이터를 가져오기
            val targetUserIds = findTargetUsersInBatch(currentTime, triggeredAt, pageNumber, BATCH_SIZE)
            if (targetUserIds.isEmpty()) break

            // Batch-size에 대해서만 메시지를 생성하고 즉시 발송
            val messagesInBatch = targetUserIds.map { userId ->
                val selectedVariation = variations.random()
                val history = NotificationHistory(
                    userId = userId,
                    messageVariationId = selectedVariation.id,
                    sentAt = LocalDateTime.now()
                )
                val savedHistory = userNotificationPort.saveHistory(history)

                NotificationMessage(
                    targetUserId = userId,
                    title = selectedVariation.titleTemplate,
                    body = selectedVariation.bodyTemplate,
                    badgeCount = 0,
                    payload = mapOf(
                        "notificationType" to NotificationType.DIARY_REMINDER.name,
                        "historyId" to savedHistory.id.toString()
                    )
                )
            }

            if (messagesInBatch.isNotEmpty()) {
                notificationPort.sendAll(messagesInBatch)
            }

            pageNumber++
        }
    }

    private fun findTargetUsersInBatch(targetTime: LocalTime, now: LocalDateTime, pageNumber: Int, pageSize: Int): List<Long> {
        val todayRange = dateHelper.getBusinessDay(now)
        val yesterdayRange = dateHelper.getBusinessDay(now.minusDays(1))

        return userNotificationPort.findDiaryReminderTargetUserIds(
            targetTime = targetTime,
            yesterdayStart = yesterdayRange.start,
            yesterdayEnd = yesterdayRange.end,
            todayStart = todayRange.start,
            todayEnd = todayRange.end,
            pageNumber = pageNumber,
            pageSize = pageSize
        )
    }
}
