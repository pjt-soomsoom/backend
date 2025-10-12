package com.soomsoom.backend.application.service.notification.strategy

import com.soomsoom.backend.application.port.out.notification.NotificationPort
import com.soomsoom.backend.common.event.Event
import com.soomsoom.backend.common.event.EventType
import com.soomsoom.backend.common.event.payload.RewardCompletedNotificationPayload
import com.soomsoom.backend.domain.notification.model.enums.NotificationType
import com.soomsoom.backend.domain.notification.model.vo.NotificationMessage
import com.soomsoom.backend.domain.reward.model.RewardType
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

/**
 * 보상 지급 시 알림 발송
 */
@Component
class RewardCompletedStrategy(
    private val notificationPort: NotificationPort,
) : NotificationStrategy<RewardCompletedNotificationPayload> {

    private val log = LoggerFactory.getLogger(javaClass)

    override fun supports(event: Event<*>) = event.eventType == EventType.REWARD_COMPLETED

    override fun execute(event: Event<RewardCompletedNotificationPayload>) {
        val payload = event.payload

        log.info("보상 알림 처리 시작, reward title = ${payload.title}")

        val message = NotificationMessage(
            targetUserId = payload.userId,
            title = payload.title,
            body = payload.body,
            badgeCount = 0,
            payload = buildMap {
                put("notificationType", NotificationType.REWARD_ACQUIRED.name)
                put("rewardType", payload.rewardType.name)
                if (payload.rewardType == RewardType.ITEM) {
                    payload.imageUrl?.let { imageUrl ->
                        put("imageUrl", imageUrl)
                    }
                } else {
                    payload.points?.let { points ->
                        put("points", points.toString())
                    }
                }
            }
        )

        log.info("보상 알림 메시지 송신 요청, title = $payload.title")
        notificationPort.send(message)
    }
}
