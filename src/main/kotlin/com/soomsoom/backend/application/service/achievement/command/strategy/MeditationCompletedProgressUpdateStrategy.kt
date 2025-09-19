package com.soomsoom.backend.application.service.achievement.command.strategy

import com.soomsoom.backend.application.port.`in`.achievement.usecase.command.CheckAndGrantAchievementsUseCase
import com.soomsoom.backend.application.port.out.achievement.AchievementPort
import com.soomsoom.backend.application.port.out.achievement.UserProgressPort
import com.soomsoom.backend.application.port.out.activityhistory.ActivityHistoryPort
import com.soomsoom.backend.common.event.payload.ActivityCompletedPayload
import com.soomsoom.backend.common.utils.DateHelper
import com.soomsoom.backend.domain.achievement.model.entity.UserProgress
import com.soomsoom.backend.domain.achievement.model.enums.ConditionType
import com.soomsoom.backend.domain.activity.model.enums.ActivityType
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import java.time.YearMonth
import java.time.temporal.ChronoUnit

@Component
@Transactional
class MeditationCompletedProgressUpdateStrategy(
    private val userProgressPort: UserProgressPort,
    private val achievementPort: AchievementPort,
    private val activityHistoryPort: ActivityHistoryPort,
    private val checkAndGrantAchievementsUseCase: CheckAndGrantAchievementsUseCase,
    private val dateHelper: DateHelper,
) : ActivityTypeProgressUpdateStrategy {
    override fun supports(): ActivityType = ActivityType.MEDITATION

    override fun update(payload: ActivityCompletedPayload) {
        // 누적 횟수
        handleProgress(payload.userId, ConditionType.MEDITATION_COUNT) { p, t -> p.increase(t) }

        // 일반 연속
        handleStreak(payload, ConditionType.MEDITATION_STREAK)

        // 심야 연속
        handleLateNightStreak(payload)

        // 월간 누적
        handleMonthlyCount(payload, ConditionType.MEDITATION_MONTHLY_COUNT)
    }

    private fun handleProgress(userId: Long, type: ConditionType, updateLogic: (UserProgress, Int) -> Unit) {
        val conditions = achievementPort.findUnachievedConditionsByType(userId, type)
        if (conditions.isEmpty()) return
        val progress = userProgressPort.findByUserIdAndType(userId, type)
            ?: UserProgress(id = null, userId = userId, type = type, currentValue = 0)
        val maxTarget = conditions.maxOfOrNull { it.targetValue } ?: 0
        updateLogic(progress, maxTarget)
        userProgressPort.save(progress)
        checkAndGrantAchievementsUseCase.checkAndGrant(userId, type)
    }

    private fun handleStreak(payload: ActivityCompletedPayload, type: ConditionType) {
        val lastLog = activityHistoryPort.findLatestCompletionLogBefore(payload.userId, payload.activityType, payload.completedAt.toLocalDate())
        val isStreak = if (lastLog?.createdAt != null) {
            val lastBusinessDate = dateHelper.getBusinessDate(lastLog.createdAt)
            val currentBusinessDate = dateHelper.getBusinessDate(payload.completedAt)
            ChronoUnit.DAYS.between(lastBusinessDate, currentBusinessDate) == 1L
        } else {
            false
        }

        handleProgress(payload.userId, type) { progress, maxTarget ->
            if (isStreak) progress.increase(maxTarget) else progress.updateTo(1, maxTarget)
        }
    }

    private fun handleLateNightStreak(payload: ActivityCompletedPayload) {
        val completedAt = payload.completedAt
        val type = ConditionType.MEDITATION_LATE_NIGHT_STREAK

        if (completedAt.hour >= 20 || completedAt.hour < 2) { // 20시 ~ 02시 사이
            handleStreak(payload, type)
        } else {
            // 심야 시간이 아니면 연속 기록을 0으로 초기화
            handleProgress(payload.userId, type) { progress, maxTarget -> progress.updateTo(0, maxTarget) }
        }
    }

    private fun handleMonthlyCount(payload: ActivityCompletedPayload, type: ConditionType) {
        val currentBusinessDate = dateHelper.getBusinessDate(payload.completedAt)
        val currentYearMonth = YearMonth.from(currentBusinessDate)
        val period = dateHelper.getBusinessPeriod(currentYearMonth)

        val monthlyCount = activityHistoryPort.countCompletionByPeriod(
            payload.userId,
            payload.activityType,
            period.start,
            period.end
        )

        handleProgress(payload.userId, type) { progress, maxTarget ->
            progress.updateTo(monthlyCount.toInt(), maxTarget)
        }
    }
}
