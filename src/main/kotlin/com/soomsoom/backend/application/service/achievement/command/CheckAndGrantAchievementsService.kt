package com.soomsoom.backend.application.service.achievement.command

import com.soomsoom.backend.application.port.`in`.achievement.usecase.command.CheckAndGrantAchievementsUseCase
import com.soomsoom.backend.application.port.out.achievement.AchievementPort
import com.soomsoom.backend.application.port.out.achievement.UserAchievedPort
import com.soomsoom.backend.application.port.out.item.ItemPort
import com.soomsoom.backend.common.event.Event
import com.soomsoom.backend.common.event.EventType
import com.soomsoom.backend.common.event.payload.AchievementAchievedNotificationPayload
import com.soomsoom.backend.common.event.payload.RewardSourcePayload
import com.soomsoom.backend.common.exception.SoomSoomException
import com.soomsoom.backend.domain.achievement.model.aggregate.Achievement
import com.soomsoom.backend.domain.achievement.model.entity.UserAchieved
import com.soomsoom.backend.domain.achievement.model.enums.ConditionType
import com.soomsoom.backend.domain.item.ItemErrorCode
import com.soomsoom.backend.domain.reward.model.RewardSource
import com.soomsoom.backend.domain.reward.model.RewardType
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
    private val itemPort: ItemPort,
) : CheckAndGrantAchievementsUseCase {

    override fun checkAndGrant(userId: Long, type: ConditionType) {
        val newlyAchievedIds = achievementPort.findNewlyAchievableEntities(userId, type)
        newlyAchievedIds.forEach { achievement ->
            grantAchievement(userId, achievement)
        }
    }

    private fun grantAchievement(userId: Long, achievement: Achievement) {
        val achieved = UserAchieved(
            id = null,
            userId = userId,
            achievementId = achievement.id,
            achievedAt = LocalDateTime.now()
        )
        userAchievedPort.save(achieved)

        val achievementNotificationEvent = Event(
            eventType = EventType.ACHIEVEMENT_ACHIEVED,
            payload = AchievementAchievedNotificationPayload(
                userId = userId,
                achievementId = achievement.id,
                achievementName = achievement.name,
                achievementGrade = achievement.grade,
                title = achievement.unlockedDisplayInfo.titleTemplate,
                body = achievement.unlockedDisplayInfo.bodyTemplate
            )
        )
        eventPublisher.publishEvent(achievementNotificationEvent)

        if (achievement.hasReward) {
            val reward = achievement.reward!!
            val rewardType = if (reward.points != null) RewardType.POINT else RewardType.ITEM

            val rewardTriggerEvent = Event(
                eventType = EventType.REWARD_SOURCE_TRIGGERED,
                payload = RewardSourcePayload(
                    userId = userId,
                    rewardType = rewardType,
                    points = reward.points,
                    itemId = reward.itemId,
                    source = RewardSource.ACHIEVEMENT,
                    sendNotification = false,
                    notificationTitle = reward.displayInfo.titleTemplate,
                    notificationBody = reward.displayInfo.bodyTemplate,
                    notificationImage = reward.itemId?.let {
                        itemPort.findById(it)?.imageUrl
                            ?: throw SoomSoomException(ItemErrorCode.NOT_FOUND)
                    }
                )
            )
            eventPublisher.publishEvent(rewardTriggerEvent)
        }
    }
}
