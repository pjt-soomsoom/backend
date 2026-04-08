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
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.time.temporal.ChronoUnit

@Component
class DiaryReminderStrategy(
    private val notificationTemplatePort: NotificationTemplatePort,
    private val userNotificationPort: UserNotificationPort,
    private val notificationPort: NotificationPort,
    private val dateHelper: DateHelper,
    private val transactionManager: org.springframework.transaction.PlatformTransactionManager,
    @Value("\${alarm.batch-size}")
    private val BATCH_SIZE: Int,
) : NotificationStrategy<SchedulerTickNotificationPayload> {

    private val KST_ZONE = ZoneId.of("Asia/Seoul")
    private val log = LoggerFactory.getLogger(javaClass)
    private val transactionTemplate = org.springframework.transaction.support.TransactionTemplate(transactionManager)

    override fun supports(event: Event<*>) = event.eventType == EventType.SCHEDULER_TICK

    override fun execute(event: Event<SchedulerTickNotificationPayload>) {
        // 'DIARY_REMINDER' 타입의 활성화된 템플릿과 Variation들을 미리 조회합니다.
        val activeTemplates = notificationTemplatePort.findActiveTemplatesWithActiveVariationsByType(NotificationType.DIARY_REMINDER)
        val variations = activeTemplates.flatMap { it.variations }
        if (variations.isEmpty()) return

        val triggeredAtUtc = event.payload.triggeredAt
        val currentTimeKst = triggeredAtUtc.atZone(dateHelper.UTC_ZONE)
            .withZoneSameInstant(KST_ZONE)
            .toLocalTime()
            .truncatedTo(ChronoUnit.MINUTES)
        var pageNumber = 0

        while (true) {
            // 1. 대상 조회 (트랜잭션 없이 조회)
            val targetUserIds = findTargetUsersInBatch(currentTimeKst, triggeredAtUtc, pageNumber, BATCH_SIZE)
            if (targetUserIds.isEmpty()) break

            // 2. 트랜잭션 범위: 히스토리 저장 및 메시지 생성
            val messagesInBatch = transactionTemplate.execute { status ->
                val histories = targetUserIds.map { userId ->
                    val selectedVariation = variations.random()
                    // NotificationHistory 엔티티 생성
                    NotificationHistory(
                        userId = userId,
                        messageVariationId = selectedVariation.id,
                        sentAt = LocalDateTime.now()
                    ) to selectedVariation
                }

                if (histories.isEmpty()) return@execute emptyList<NotificationMessage>()

                // Bulk Insert!
                val savedHistories = userNotificationPort.saveAllHistories(histories.map { it.first })

                // 메시지 목록 생성
                savedHistories.mapIndexed { index, savedHistory ->
                    val selectedVariation = histories[index].second
                    NotificationMessage(
                        targetUserId = savedHistory.userId,
                        title = selectedVariation.titleTemplate,
                        body = selectedVariation.bodyTemplate,
                        badgeCount = 0,
                        payload = mapOf(
                            "notificationType" to NotificationType.DIARY_REMINDER.name,
                            "historyId" to savedHistory.id.toString()
                        )
                    )
                }
            } ?: emptyList()

            // 3. 메시지 발송 (트랜잭션 외부)
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
