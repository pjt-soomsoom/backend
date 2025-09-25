package com.soomsoom.backend.application.service.notification.strategy

import com.soomsoom.backend.application.port.out.achievement.AchievementPort
import com.soomsoom.backend.application.port.out.notification.NotificationPort
import com.soomsoom.backend.common.event.Event
import com.soomsoom.backend.common.event.EventType
import com.soomsoom.backend.common.event.payload.AchievementAchievedNotificationPayload
import com.soomsoom.backend.common.exception.SoomSoomException
import com.soomsoom.backend.domain.achievement.AchievementErrorCode
import com.soomsoom.backend.domain.notification.model.enums.NotificationType
import com.soomsoom.backend.domain.notification.model.vo.NotificationMessage
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
class AchievementUnlockedStrategy(
    private val notificationPort: NotificationPort,
    private val achievementPort: AchievementPort,
) : NotificationStrategy<AchievementAchievedNotificationPayload> {

    private val log = LoggerFactory.getLogger(javaClass)

    /**
     * 이 전략은 'ACHIEVEMENT_ACHIEVED' 타입의 이벤트만 처리하도록 명시
     */
    override fun supports(event: Event<*>) = event.eventType == EventType.ACHIEVEMENT_ACHIEVED

    /**
     * 업적 달성 이벤트를 기반으로 푸시 알림 메시지를 생성
     */
    @Transactional
    override fun execute(event: Event<AchievementAchievedNotificationPayload>) {
        val payload = event.payload
        val achievement = achievementPort.findById(payload.achievementId)
            ?: throw SoomSoomException(AchievementErrorCode.NOT_FOUND)

        log.info("업적 달성 처리 시작, achievementName = ${payload.achievementName}")

        val message = NotificationMessage(
            targetUserId = payload.userId,
            title = payload.title,
            body = payload.body,
            badgeCount = 0,
            payload = buildMap {
                put("notificationType", NotificationType.ACHIEVEMENT_UNLOCKED.name)
                put("achievementId", payload.achievementId.toString())
                put("achievementGrade", payload.achievementGrade.name)
                if (achievement.hasReward) {
                    put("points", achievement.reward!!.points.toString())
                }
            }
        )

        log.info("업적 달성 메시지 송신 요청, title = $payload.title")
        notificationPort.send(message)
    }
}
