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
import org.springframework.transaction.support.TransactionTemplate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId

@Component
class UserInactiveStrategy(
    private val notificationTemplatePort: NotificationTemplatePort,
    private val userNotificationPort: UserNotificationPort,
    private val notificationPort: NotificationPort,
    private val dateHelper: DateHelper,
    private val transactionTemplate: TransactionTemplate,
    @Value("\${alarm.inactive-user.default-time}")
    val defaultTime: LocalTime,
) : NotificationStrategy<SchedulerTickNotificationPayload> {

    private val log = LoggerFactory.getLogger(javaClass)
    private val KST_ZONE = ZoneId.of("Asia/Seoul")

    override fun supports(event: Event<*>) = event.eventType == EventType.SCHEDULER_TICK

    override fun execute(event: Event<SchedulerTickNotificationPayload>) {
        val triggeredAtUtc = event.payload.triggeredAt
        val activeTemplates = notificationTemplatePort.findActiveTemplatesWithActiveVariationsByType(NotificationType.RE_ENGAGEMENT)
        if (activeTemplates.isEmpty()) return

        val inactivityConditions = activeTemplates.associate {
            val businessDay = dateHelper.getBusinessDay(triggeredAtUtc.minusDays(it.triggerCondition?.toLong()!! - 1))
            val inactiveDays = it.triggerCondition!!
            val dateRange = Pair(businessDay.start, businessDay.end)
            inactiveDays to dateRange
        }

        // [Optimized] 1000개 단위 청크 처리
        var offset = 0
        val BATCH_SIZE = 1000

        while (true) {
            val targetUsers = userNotificationPort.findReEngagementTargets(
                inactivityConditions,
                offset,
                BATCH_SIZE
            )
            if (targetUsers.isEmpty()) break

            // 트랜잭션 범위 최소화: Chunk 단위로 실행
            val messages = transactionTemplate.execute {
                val chunkHistories = targetUsers.map { user ->
                    val matchedTemplate = activeTemplates.find { it.triggerCondition == user.inactiveDays }
                    val selectedVariation = matchedTemplate?.variations?.random()!!

                    NotificationHistory(
                        userId = user.userId,
                        messageVariationId = selectedVariation.id,
                        sentAt = LocalDateTime.now()
                    ) to selectedVariation
                }

                // [Bulk Insert]
                val savedHistories = userNotificationPort.saveAllHistories(chunkHistories.map { it.first })

                // 메시지 생성
                savedHistories.zip(chunkHistories).map { (history, pair) ->
                    val variation = pair.second
                    NotificationMessage(
                        targetUserId = history.userId,
                        title = variation.titleTemplate,
                        body = variation.bodyTemplate,
                        badgeCount = 0,
                        payload = mapOf(
                            "notificationType" to NotificationType.RE_ENGAGEMENT.name,
                            "historyId" to history.id.toString()
                        )
                    )
                }
            } ?: emptyList()

            // FCM 발송
            if (messages.isNotEmpty()) {
                notificationPort.sendAll(messages)
            }

            offset += BATCH_SIZE
        }
    }
}
