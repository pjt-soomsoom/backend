package com.soomsoom.backend.application.service.achievement.command

import com.soomsoom.backend.application.port.`in`.achievement.usecase.command.CheckAndGrantAchievementsUseCase
import com.soomsoom.backend.application.port.out.achievement.AchievementPort
import com.soomsoom.backend.application.port.out.achievement.UserAchievedPort
import com.soomsoom.backend.common.event.Event
import com.soomsoom.backend.common.event.EventType
import com.soomsoom.backend.common.event.payload.AchievementAchievedNotificationPayload
import com.soomsoom.backend.domain.achievement.model.ConditionType
import com.soomsoom.backend.domain.achievement.model.UserAchieved
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Service
@Transactional
class CheckAndGrantAchievementsService(
    private val achievementPort: AchievementPort,
    private val userAchievedPort: UserAchievedPort,
    private val eventPublisher: ApplicationEventPublisher,
) : CheckAndGrantAchievementsUseCase {

    override fun checkAndGrant(userId: Long, type: ConditionType) {
        val newlyAchievedIds = achievementPort.findNewlyAchievableIds(userId, type)
        newlyAchievedIds.forEach { achievementId ->
            grantAchievement(userId, achievementId)
        }
    }

    private fun grantAchievement(userId: Long, achievementId: Long) {
        val achieved = UserAchieved(
            id = null,
            userId = userId,
            achievementId = achievementId,
            achievedAt = LocalDateTime.now()
        )
        userAchievedPort.save(achieved)

        val event = Event(
            eventType = EventType.ACHIEVEMENT_ACHIEVED,
            payload = AchievementAchievedNotificationPayload(userId, achievementId)
        )
        eventPublisher.publishEvent(event)
    }
}
