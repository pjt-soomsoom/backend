// src/main/kotlin/com/soomsoom/backend/application/service/achievement/command/strategy/DiaryCreatedProgressUpdateStrategy.kt
package com.soomsoom.backend.application.service.achievement.command.strategy

import com.soomsoom.backend.application.port.`in`.achievement.usecase.command.CheckAndGrantAchievementsUseCase
import com.soomsoom.backend.application.port.out.achievement.AchievementPort
import com.soomsoom.backend.application.port.out.achievement.UserProgressPort
import com.soomsoom.backend.application.port.out.diary.DiaryPort
import com.soomsoom.backend.common.event.Event
import com.soomsoom.backend.common.event.Payload
import com.soomsoom.backend.common.event.payload.DiaryCreatedPayload
import com.soomsoom.backend.domain.achievement.model.ConditionType
import com.soomsoom.backend.domain.achievement.model.UserProgress
import com.soomsoom.backend.domain.diary.model.Emotion
import org.springframework.stereotype.Component

@Component
class DiaryCreatedProgressUpdateStrategy(
    private val userProgressPort: UserProgressPort,
    private val achievementPort: AchievementPort,
    private val diaryPort: DiaryPort,
    private val checkAndGrantAchievementsUseCase: CheckAndGrantAchievementsUseCase,
) : ProgressUpdateStrategy {
    override fun supports(event: Event<out Payload>) = event.payload is DiaryCreatedPayload

    override fun update(event: Event<out Payload>) {
        val payload = event.payload as DiaryCreatedPayload

        handleProgress(payload.userId, ConditionType.DIARY_COUNT) { progress, maxTarget -> progress.increase(maxTarget) }
        handleStreakProgress(payload)
        handleMonthlyCountProgress(payload)
        handleEmotionOvercome(payload)
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

    private fun handleStreakProgress(payload: DiaryCreatedPayload) {
        val yesterdayDiaryExists = diaryPort.existsByUserIdAndRecordDate(payload.userId, payload.recordDate.minusDays(1))
        handleProgress(payload.userId, ConditionType.DIARY_STREAK) { progress, maxTarget ->
            if (yesterdayDiaryExists) progress.increase(maxTarget) else progress.updateTo(1, maxTarget)
        }
    }

    private fun handleMonthlyCountProgress(payload: DiaryCreatedPayload) {
        val firstDayOfMonth = payload.recordDate.withDayOfMonth(1)
        val monthlyCount = diaryPort.countByUserIdAndDateBetween(payload.userId, firstDayOfMonth, payload.recordDate)
        handleProgress(payload.userId, ConditionType.DIARY_MONTHLY_COUNT) { progress, maxTarget ->
            progress.updateTo(monthlyCount.toInt(), maxTarget)
        }
    }

    private fun handleEmotionOvercome(payload: DiaryCreatedPayload) {
        if (payload.emotion != Emotion.HAPPINESS) return

        // 바로 직전 일기의 감정을 조회
        val previousDiary = diaryPort.findLatestBefore(payload.userId, payload.recordDate)
        if (previousDiary?.emotion == Emotion.ANGER) { // 최악(ANGER) -> 최고(HAPPINESS)
            handleProgress(payload.userId, ConditionType.HIDDEN_EMOTION_OVERCOME) { progress, maxTarget ->
                progress.updateTo(1, maxTarget) // 달성했다는 의미로 1로 설정
            }
        }
    }
}
