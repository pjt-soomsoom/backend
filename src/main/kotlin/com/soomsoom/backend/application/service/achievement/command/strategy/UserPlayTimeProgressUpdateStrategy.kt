// src/main/kotlin/com/soomsoom/backend/application/service/achievement/command/strategy/UserPlayTimeProgressUpdateStrategy.kt
package com.soomsoom.backend.application.service.achievement.command.strategy

import com.soomsoom.backend.application.port.`in`.achievement.usecase.command.CheckAndGrantAchievementsUseCase
import com.soomsoom.backend.application.port.out.achievement.AchievementPort
import com.soomsoom.backend.application.port.out.achievement.UserProgressPort
import com.soomsoom.backend.common.event.Event
import com.soomsoom.backend.common.event.Payload
import com.soomsoom.backend.common.event.payload.UserPlayTimeAccumulatedPayload
import com.soomsoom.backend.domain.achievement.model.ConditionType
import com.soomsoom.backend.domain.achievement.model.UserProgress
import com.soomsoom.backend.domain.activity.model.enums.ActivityType
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
@Transactional
class UserPlayTimeProgressUpdateStrategy(
    private val userProgressPort: UserProgressPort,
    private val achievementPort: AchievementPort,
    private val checkAndGrantAchievementsUseCase: CheckAndGrantAchievementsUseCase,
) : ProgressUpdateStrategy {

    override fun supports(event: Event<out Payload>) = event.payload is UserPlayTimeAccumulatedPayload

    override fun update(event: Event<out Payload>) {
        val payload = event.payload as UserPlayTimeAccumulatedPayload

        // 활동 타입에 따라 적절한 ConditionType을 선택
        val type = when (payload.activityType) {
            ActivityType.MEDITATION -> ConditionType.MEDITATION_TOTAL_SECONDS
            ActivityType.BREATHING -> ConditionType.BREATHING_TOTAL_SECONDS
            ActivityType.SOUND_EFFECT -> ConditionType.SOUND_EFFECT_TOTAL_SECONDS
        }

        val conditions = achievementPort.findUnachievedConditionsByType(payload.userId, type)
        if (conditions.isEmpty()) return

        val progress = userProgressPort.findByUserIdAndType(payload.userId, type)
            ?: UserProgress(id = null, userId = payload.userId, type = type, currentValue = 0)

        val maxTarget = conditions.maxOfOrNull { it.targetValue } ?: 0
        val nextValue = progress.currentValue + payload.actualPlayTimeInSeconds
        progress.updateTo(nextValue, maxTarget)

        userProgressPort.save(progress)
        checkAndGrantAchievementsUseCase.checkAndGrant(payload.userId, type)
    }
}
