package com.soomsoom.backend.application.service.notification.strategy

import com.soomsoom.backend.application.port.out.item.ItemPort
import com.soomsoom.backend.application.port.out.notification.NotificationPort
import com.soomsoom.backend.common.event.Event
import com.soomsoom.backend.common.event.EventType
import com.soomsoom.backend.common.event.payload.MissionCompletedNotificationPayload
import com.soomsoom.backend.common.exception.SoomSoomException
import com.soomsoom.backend.domain.item.ItemErrorCode
import com.soomsoom.backend.domain.notification.model.enums.NotificationType
import com.soomsoom.backend.domain.notification.model.vo.NotificationMessage
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Component
class MissionCompletedStrategy(
    private val notificationPort: NotificationPort,
    private val itemPort: ItemPort,
) : NotificationStrategy<MissionCompletedNotificationPayload> {
    private val log = LoggerFactory.getLogger(javaClass)

    /**
     * 이 전략은 'MISSION_COMPLETED' 타입의 이벤트만 처리하도록 명시
     */
    override fun supports(event: Event<*>) = event.eventType == EventType.MISSION_COMPLETED

    /**
     * 미션 달성 이벤트를 기반으로 푸시 알림 메시지를 생성
     */
    override fun execute(event: Event<MissionCompletedNotificationPayload>) {
        val payload = event.payload

        log.info("미션 달성 처리 시작, missionName = ${payload.missionName}")

        val message = NotificationMessage(
            targetUserId = payload.userId,
            title = payload.title,
            body = payload.body,
            badgeCount = 0,
            payload = buildMap {
                put("notificationType", NotificationType.MISSION_COMPLETED.name)
                put("missionId", payload.missionId.toString())
                if (payload.reward.points != null) {
                    put("points", payload.reward.points.toString())
                } else {
                    put(
                        "imageUrl",
                        itemPort.findById(payload.reward.itemId!!)?.imageUrl
                            ?: throw SoomSoomException(ItemErrorCode.NOT_FOUND)
                    )
                }
            }
        )

        log.info("미션 달성 메시지 송신 요청, title = $payload.title")
        notificationPort.send(message)
    }
}
