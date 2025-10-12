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
class UserInactiveStrategy(
    private val notificationTemplatePort: NotificationTemplatePort,
    private val userNotificationPort: UserNotificationPort,
    private val notificationPort: NotificationPort,
    private val dateHelper: DateHelper,
    @Value("\${alarm.inactive-user.default-time}")
    val defaultTime: LocalTime,
    @Value("\${alarm.batch-size}")
    private val BATCH_SIZE: Int,
) : NotificationStrategy<SchedulerTickNotificationPayload> {

    private val log = LoggerFactory.getLogger(javaClass)

    override fun supports(event: Event<*>) = event.eventType == EventType.SCHEDULER_TICK

    @Transactional
    override fun execute(event: Event<SchedulerTickNotificationPayload>) {
        val now = event.payload.triggeredAt

        val currentTimeTruncated = now.toLocalTime().truncatedTo(ChronoUnit.MINUTES)
        val defaultTimeTruncated = defaultTime.truncatedTo(ChronoUnit.MINUTES)

        if (currentTimeTruncated != defaultTimeTruncated) {
            return
        }

        val activeTemplates = notificationTemplatePort.findActiveTemplatesWithActiveVariationsByType(NotificationType.RE_ENGAGEMENT)
        if (activeTemplates.isEmpty()) return

        val inactivityConditions = activeTemplates.associate {
            val businessDay = dateHelper.getBusinessDay(now.minusDays(it.triggerCondition?.toLong()!! - 1))
            val inactiveDays = it.triggerCondition!!
            val dateRange = Pair(businessDay.start, businessDay.end)
            inactiveDays to dateRange
        }

        var pageNumber = 0
        while (true) {
            val targetUsers = userNotificationPort.findReEngagementTargets(
                inactivityConditions,
                pageNumber,
                BATCH_SIZE
            )
            if (targetUsers.isEmpty()) break

            val messagesInBatch = targetUsers.mapNotNull { user ->
                val matchedTemplate = activeTemplates.find { it.triggerCondition == user.inactiveDays }
                val targetVariations = matchedTemplate?.variations
                if (targetVariations.isNullOrEmpty()) {
                    return@mapNotNull null
                }

                val selectedVariation = targetVariations.random()
                val history = NotificationHistory(
                    userId = user.userId,
                    messageVariationId = selectedVariation.id,
                    sentAt = LocalDateTime.now()
                )
                val savedHistory = userNotificationPort.saveHistory(history)

                NotificationMessage(
                    targetUserId = user.userId,
                    title = selectedVariation.titleTemplate,
                    body = selectedVariation.bodyTemplate,
                    badgeCount = 0,
                    payload = mapOf(
                        "notificationType" to NotificationType.RE_ENGAGEMENT.name,
                        "historyId" to savedHistory.id.toString(),
                        "sound" to "cat-meow-short-push.wav"
                    )
                )
            }

            if (messagesInBatch.isNotEmpty()) {
                notificationPort.sendAll(messagesInBatch)
            }

            pageNumber++
        }
    }
}
