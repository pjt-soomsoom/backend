package com.soomsoom.backend.application.service.notification.strategy

import com.soomsoom.backend.application.port.out.notification.NotificationPort
import com.soomsoom.backend.application.port.out.notification.NotificationTemplatePort
import com.soomsoom.backend.application.port.out.notification.UserNotificationPort
import com.soomsoom.backend.application.port.out.useractivity.ConnectionLogPort
import com.soomsoom.backend.common.event.Event
import com.soomsoom.backend.common.event.EventType
import com.soomsoom.backend.common.event.payload.SchedulerTickNotificationPayload
import com.soomsoom.backend.common.utils.DateHelper
import com.soomsoom.backend.domain.notification.model.entity.NotificationHistory
import com.soomsoom.backend.domain.notification.model.enums.NotificationType
import com.soomsoom.backend.domain.notification.model.vo.NotificationMessage
import org.springframework.stereotype.Component
import java.time.LocalDateTime

@Component
class UserInactiveStrategy(
    private val notificationTemplatePort: NotificationTemplatePort,
    private val userNotificationPort: UserNotificationPort,
    private val connectionLogPort: ConnectionLogPort,
    private val notificationPort: NotificationPort,
    private val dateHelper: DateHelper,
) : NotificationStrategy<SchedulerTickNotificationPayload> {

    companion object {
        private const val BATCH_SIZE = 1000
    }

    override fun supports(event: Event<*>) = event.eventType == EventType.SCHEDULER_TICK

    override fun execute(event: Event<SchedulerTickNotificationPayload>) {
        val now = event.payload.triggeredAt

        if (now.hour != 21) {
            return
        }

        // '재방문 유도(RE_ENGAGEMENT)' 타입의 모든 템플릿 그룹(1일차, 3일차 등)을 조회
        val activeTemplates = notificationTemplatePort.findActiveTemplatesWithActiveVariationsByType(NotificationType.RE_ENGAGEMENT)
        if (activeTemplates.isEmpty()) return

        // ConnectionLogPort에 넘길 맵을 동적으로 생성
        val inactivityConditions = activeTemplates.associate {
            val businessDay = dateHelper.getBusinessDay(now.minusDays(it.triggerCondition?.toLong()!! - 1))
            val inactiveDays = it.triggerCondition!!
            val dateRange = Pair(businessDay.start, businessDay.end)
            inactiveDays to dateRange
        }

        // 페이징 처리를 위해 반복 실행
        var pageNumber = 0
        while (true) {
            // ConnectionLogPort를 통해 조건에 맞는 미접속 사용자 목록을 한 번에 조회
            val inactiveUsers = connectionLogPort.findInactiveUsers(inactivityConditions, pageNumber, BATCH_SIZE)
            if (inactiveUsers.isEmpty()) break

            // 각 미접속 사용자별로 적절한 메시지를 생성하여 즉시 발송
            val messagesInBatch = inactiveUsers.mapNotNull { user ->
                val settings = userNotificationPort.findSettingsByUserId(user.userId)
                if (settings?.reEngagementNotificationEnabled == false) {
                    return@mapNotNull null
                }

                // 사용자의 미접속 일수에 맞는 템플릿 그룹을 찾기
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
}
