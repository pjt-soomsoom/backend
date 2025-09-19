// src/main/kotlin/com/soomsoom/backend/application/service/achievement/command/strategy/DiaryCreatedProgressUpdateStrategy.kt
package com.soomsoom.backend.application.service.achievement.command.strategy

import com.soomsoom.backend.application.port.`in`.achievement.usecase.command.CheckAndGrantAchievementsUseCase
import com.soomsoom.backend.application.port.out.achievement.AchievementPort
import com.soomsoom.backend.application.port.out.achievement.UserProgressPort
import com.soomsoom.backend.application.port.out.diary.DiaryPort
import com.soomsoom.backend.common.event.Event
import com.soomsoom.backend.common.event.Payload
import com.soomsoom.backend.common.event.payload.DiaryCreatedPayload
import com.soomsoom.backend.common.utils.DateHelper
import com.soomsoom.backend.domain.achievement.model.entity.UserProgress
import com.soomsoom.backend.domain.achievement.model.enums.ConditionType
import com.soomsoom.backend.domain.common.DeletionStatus
import com.soomsoom.backend.domain.diary.model.Emotion
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
@Transactional
class DiaryCreatedProgressUpdateStrategy(
    private val userProgressPort: UserProgressPort,
    private val achievementPort: AchievementPort,
    private val diaryPort: DiaryPort,
    private val checkAndGrantAchievementsUseCase: CheckAndGrantAchievementsUseCase,
    private val dateHelper: DateHelper,
) : ProgressUpdateStrategy {
    override fun supports(event: Event<out Payload>) = event.payload is DiaryCreatedPayload

    override fun update(event: Event<out Payload>) {
        val payload = event.payload as DiaryCreatedPayload

        handleProgress(payload.userId, ConditionType.DIARY_COUNT) { progress, maxTarget -> progress.increase(maxTarget) }

        // 연속 달성 관련 로직
        handleStreakProgress(payload)

        // 월간 누적 관련 로직
        handleMonthlyCountProgress(payload)

        // 감정 극복 히든 업적
        handleEmotionOvercome(payload)
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

    private fun handleStreakProgress(payload: DiaryCreatedPayload) {
        val businessDay = dateHelper.getBusinessDay(payload.createdAt.minusDays(1))
        val yesterdayDiaryExists = diaryPort.existsByUserIdAndCreatedAtBetween(payload.userId, businessDay.start, businessDay.end)
        handleProgress(payload.userId, ConditionType.DIARY_STREAK) { progress, maxTarget ->
            if (yesterdayDiaryExists) progress.increase(maxTarget) else progress.updateTo(1, maxTarget)
        }
    }

    private fun handleMonthlyCountProgress(payload: DiaryCreatedPayload) {
        val currentBusinessDate = dateHelper.getBusinessDate(payload.createdAt)
        val firstDayOfMonth = currentBusinessDate.withDayOfMonth(1)
        val businessPeriod = dateHelper.getBusinessPeriod(firstDayOfMonth, currentBusinessDate)

        val monthlyCount = diaryPort.countByUserIdAndCreatedAtBetween(
            payload.userId,
            businessPeriod.start,
            businessPeriod.end,
            deletionStatus = DeletionStatus.ACTIVE
        )
        handleProgress(payload.userId, ConditionType.DIARY_MONTHLY_COUNT) { progress, maxTarget ->
            progress.updateTo(monthlyCount.toInt(), maxTarget)
        }
    }

    private fun handleEmotionOvercome(payload: DiaryCreatedPayload) {
        if (payload.emotion != Emotion.HAPPINESS) return

        // 바로 직전 일기의 감정을 조회
        val previousDiary = diaryPort.findLatestBefore(payload.userId, payload.createdAt)
        if (previousDiary?.emotion == Emotion.ANGER) { // 최악(ANGER) -> 최고(HAPPINESS)
            handleProgress(payload.userId, ConditionType.HIDDEN_EMOTION_OVERCOME) { progress, maxTarget ->
                progress.updateTo(1, maxTarget) // 달성했다는 의미로 1로 설정
            }
        }
    }
}
