package com.soomsoom.backend.application.service.achievement.command.strategy

import com.soomsoom.backend.application.port.`in`.achievement.usecase.command.CheckAndGrantAchievementsUseCase
import com.soomsoom.backend.application.port.out.achievement.AchievementPort
import com.soomsoom.backend.application.port.out.achievement.UserProgressPort
import com.soomsoom.backend.common.event.Event
import com.soomsoom.backend.common.event.Payload
import com.soomsoom.backend.common.event.payload.ScreenTimeAccumulatedPayload
import com.soomsoom.backend.domain.achievement.model.entity.UserProgress
import com.soomsoom.backend.domain.achievement.model.enums.ConditionType
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
@Transactional
class ScreenTimeProgressUpdateStrategy(
    private val userProgressPort: UserProgressPort,
    private val achievementPort: AchievementPort,
    private val checkAndGrantAchievementsUseCase: CheckAndGrantAchievementsUseCase,
) : ProgressUpdateStrategy {
    override fun supports(event: Event<out Payload>) = event.payload is ScreenTimeAccumulatedPayload

    override fun update(event: Event<out Payload>) {
        val payload = event.payload as ScreenTimeAccumulatedPayload
        val type = ConditionType.HIDDEN_STAY_HOME_SCREEN

        val conditions = achievementPort.findUnachievedConditionsByType(payload.userId, type)
        if (conditions.isEmpty()) {
            // 더 이상 달성할 관련 업적이 없으므로, 진행도를 누적할 필요가 없음
            return
        }

        val progress = userProgressPort.findByUserIdAndType(payload.userId, type)
            ?: UserProgress(id = null, userId = payload.userId, type = type, currentValue = 0)

        val maxTarget = conditions.maxOfOrNull { it.targetValue } ?: 0

        val nextValue = progress.currentValue + payload.durationInSeconds
        progress.updateTo(nextValue, maxTarget)

        userProgressPort.save(progress)

        // 업적 달성 여부를 확인하고, 달성했다면 보상을 지급하는 프로세스를 트리거
        checkAndGrantAchievementsUseCase.checkAndGrant(payload.userId, type)
    }
}
