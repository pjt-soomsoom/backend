package com.soomsoom.backend.application.service.achievement.command.strategy

import com.soomsoom.backend.application.port.`in`.achievement.usecase.command.CheckAndGrantAchievementsUseCase
import com.soomsoom.backend.application.port.out.achievement.AchievementPort
import com.soomsoom.backend.application.port.out.achievement.UserProgressPort
import com.soomsoom.backend.common.event.Event
import com.soomsoom.backend.common.event.Payload
import com.soomsoom.backend.common.event.payload.ItemsEquippedPayload
import com.soomsoom.backend.domain.achievement.model.entity.UserProgress
import com.soomsoom.backend.domain.achievement.model.enums.ConditionType
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
@Transactional
class CharacterCustomizedProgressUpdateStrategy(
    private val userProgressPort: UserProgressPort,
    private val achievementPort: AchievementPort,
    private val checkAndGrantAchievementsUseCase: CheckAndGrantAchievementsUseCase,
) : ProgressUpdateStrategy {
    override fun supports(event: Event<out Payload>) = event.payload is ItemsEquippedPayload

    override fun update(event: Event<out Payload>) {
        val payload = event.payload as ItemsEquippedPayload
        val type = ConditionType.HIDDEN_CUSTOMIZE_CHARACTER

        val conditions = achievementPort.findUnachievedConditionsByType(payload.userId, type)
        if (conditions.isEmpty()) return

        val progress = userProgressPort.findByUserIdAndType(payload.userId, type)
            ?: UserProgress(id = null, userId = payload.userId, type = type, currentValue = 0)

        val maxTarget = conditions.maxOfOrNull { it.targetValue } ?: 0

        // 꾸미기 횟수는 단순히 1을 더함
        progress.increase(maxTarget)

        userProgressPort.save(progress)

        checkAndGrantAchievementsUseCase.checkAndGrant(payload.userId, type)
    }
}
