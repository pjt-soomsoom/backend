package com.soomsoom.backend.application.service.achievement.command.strategy

import com.soomsoom.backend.application.port.`in`.achievement.usecase.command.CheckAndGrantAchievementsUseCase
import com.soomsoom.backend.application.port.out.achievement.AchievementPort
import com.soomsoom.backend.application.port.out.achievement.UserProgressPort
import com.soomsoom.backend.application.port.out.activityhistory.ActivityHistoryPort
import com.soomsoom.backend.common.event.payload.ActivityCompletedPayload
import com.soomsoom.backend.domain.achievement.model.ConditionType
import com.soomsoom.backend.domain.achievement.model.UserProgress
import com.soomsoom.backend.domain.activity.model.enums.ActivityType
import org.springframework.stereotype.Component
import java.time.temporal.ChronoUnit

@Component
class BreathingCompletedProgressUpdateStrategy(
    private val userProgressPort: UserProgressPort,
    private val achievementPort: AchievementPort,
    private val activityHistoryPort: ActivityHistoryPort,
    private val checkAndGrantAchievementsUseCase: CheckAndGrantAchievementsUseCase,
) : ActivityTypeProgressUpdateStrategy {
    override fun supports(): ActivityType = ActivityType.BREATHING

    override fun update(payload: ActivityCompletedPayload) {
        handleProgress(payload.userId, ConditionType.BREATHING_COUNT) { p, t -> p.increase(t) }
        handleStreak(payload, ConditionType.BREATHING_STREAK)
        handleMultiTypeCount(payload.userId)
    }

    private fun handleProgress(userId: Long, type: ConditionType, updateLogic: (UserProgress, Int) -> Unit) {
        val conditions = achievementPort.findConditionsByType(type)
        if (conditions.isEmpty()) return
        val progress = userProgressPort.findByUserIdAndType(userId, type)
            ?: UserProgress(id = null, userId = userId, type = type, currentValue = 0)
        val maxTarget = conditions.maxOfOrNull { it.targetValue } ?: 0
        updateLogic(progress, maxTarget)
        userProgressPort.save(progress)
        checkAndGrantAchievementsUseCase.checkAndGrant(userId, type)
    }

    private fun handleStreak(payload: ActivityCompletedPayload, type: ConditionType) {
        val lastLog = activityHistoryPort.findLatestCompletionLog(payload.userId, payload.activityType, payload.completedAt.toLocalDate())
        val isStreak = lastLog != null && ChronoUnit.DAYS.between(lastLog.createdAt!!.toLocalDate(), payload.completedAt.toLocalDate()) == 1L

        handleProgress(payload.userId, type) { progress, maxTarget ->
            if (isStreak) progress.increase(maxTarget) else progress.updateTo(1, maxTarget)
        }
    }

    private fun handleMultiTypeCount(userId: Long) {
        val type = ConditionType.BREATHING_MULTI_TYPE_COUNT
        val distinctBreathingCount = activityHistoryPort.countDistinctActivity(userId, ActivityType.BREATHING)

        handleProgress(userId, type) { progress, maxTarget ->
            progress.updateTo(distinctBreathingCount.toInt(), maxTarget)
        }
    }
}
