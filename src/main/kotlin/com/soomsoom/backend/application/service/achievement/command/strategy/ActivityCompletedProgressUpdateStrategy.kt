// src/main/kotlin/com/soomsoom/backend/application/service/achievement/command/strategy/ActivityCompletedProgressUpdateStrategy.kt
package com.soomsoom.backend.application.service.achievement.command.strategy

import com.soomsoom.backend.application.port.`in`.achievement.usecase.command.CheckAndGrantAchievementsUseCase
import com.soomsoom.backend.application.port.out.achievement.AchievementPort
import com.soomsoom.backend.application.port.out.achievement.UserProgressPort
import com.soomsoom.backend.application.port.out.activityhistory.ActivityHistoryPort
import com.soomsoom.backend.common.event.Event
import com.soomsoom.backend.common.event.Payload
import com.soomsoom.backend.common.event.payload.ActivityCompletedNotificationPayload
import com.soomsoom.backend.domain.achievement.model.ConditionType
import com.soomsoom.backend.domain.achievement.model.UserProgress
import com.soomsoom.backend.domain.activity.model.ActivityType
import org.springframework.stereotype.Component
import java.time.temporal.ChronoUnit

@Component
class ActivityCompletedProgressUpdateStrategy(
    private val userProgressPort: UserProgressPort,
    private val achievementPort: AchievementPort,
    private val activityHistoryPort: ActivityHistoryPort,
    private val checkAndGrantAchievementsUseCase: CheckAndGrantAchievementsUseCase,
) : ProgressUpdateStrategy {

    override fun supports(event: Event<out Payload>) = event.payload is ActivityCompletedNotificationPayload

    override fun update(event: Event<out Payload>) {
        val payload = event.payload as ActivityCompletedNotificationPayload

        when (payload.activityType) {
            ActivityType.MEDITATION -> {
                handleProgress(payload.userId, ConditionType.MEDITATION_COUNT) { p, t -> p.increase(t) }
                handleStreak(payload, ConditionType.MEDITATION_STREAK)
                handleLateNightStreak(payload, ConditionType.MEDITATION_LATE_NIGHT_STREAK)
                handleMonthlyCount(payload, ConditionType.MEDITATION_MONTHLY_COUNT)
            }
            ActivityType.BREATHING -> {
                handleProgress(payload.userId, ConditionType.BREATHING_COUNT) { p, t -> p.increase(t) }
                handleStreak(payload, ConditionType.BREATHING_STREAK)
                handleMultiTypeCount(payload.userId)
            }
        }
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

    private fun handleStreak(payload: ActivityCompletedNotificationPayload, type: ConditionType) {
        val lastLog = activityHistoryPort.findLatestCompletionLog(payload.userId, payload.activityType, payload.completedAt.toLocalDate())
        val isStreak = lastLog != null && ChronoUnit.DAYS.between(lastLog.createdAt!!.toLocalDate(), payload.completedAt.toLocalDate()) == 1L

        handleProgress(payload.userId, type) { progress, maxTarget ->
            if (isStreak) progress.increase(maxTarget) else progress.updateTo(1, maxTarget)
        }
    }

    private fun handleLateNightStreak(payload: ActivityCompletedNotificationPayload, type: ConditionType) {
        val completedAt = payload.completedAt
        if (completedAt.hour >= 20 || completedAt.hour < 2) {
            handleStreak(payload, type)
        } else {
            handleProgress(payload.userId, type) { progress, maxTarget -> progress.updateTo(0, maxTarget) }
        }
    }

    private fun handleMonthlyCount(payload: ActivityCompletedNotificationPayload, type: ConditionType) {
        val date = payload.completedAt.toLocalDate()
        val firstDayOfMonth = date.withDayOfMonth(1)
        val monthlyCount = activityHistoryPort.countMonthlyCompletion(payload.userId, payload.activityType, firstDayOfMonth, date)

        handleProgress(payload.userId, type) { progress, maxTarget ->
            progress.updateTo(monthlyCount.toInt(), maxTarget)
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
