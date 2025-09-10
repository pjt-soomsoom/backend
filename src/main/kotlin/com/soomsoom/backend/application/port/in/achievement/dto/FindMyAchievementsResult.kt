package com.soomsoom.backend.application.port.`in`.achievement.dto

import com.soomsoom.backend.domain.achievement.model.Achievement
import com.soomsoom.backend.domain.achievement.model.AchievementCategory
import com.soomsoom.backend.domain.achievement.model.AchievementGrade
import com.soomsoom.backend.domain.achievement.model.UserAchieved
import com.soomsoom.backend.domain.achievement.model.UserProgress
import java.time.LocalDateTime

data class FindMyAchievementsResult(
    val achievementId: Long,
    val name: String,
    val description: String,
    val phrase: String?,
    val grade: AchievementGrade,
    val category: AchievementCategory,
    val isAchieved: Boolean,
    val achievedAt: LocalDateTime?,
    val progress: ProgressInfo?,
) {
    data class ProgressInfo(
        val currentValue: Int,
        val targetValue: Int,
    )

    companion object {
        fun of(
            achievement: Achievement,
            userAchieved: UserAchieved?,
            userProgress: UserProgress?,
            targetValue: Int,
        ): FindMyAchievementsResult {
            val progressInfo = if (userAchieved != null) {
                // 달성 완료 시, 진행도를 꽉 채워서(예: 10/10) 보여줌
                ProgressInfo(currentValue = targetValue, targetValue = targetValue)
            } else {
                // 미달성 시, 현재 진행도를 보여줌
                userProgress?.let {
                    ProgressInfo(currentValue = it.currentValue, targetValue = targetValue)
                } ?: ProgressInfo(currentValue = 0, targetValue = targetValue)
            }

            return FindMyAchievementsResult(
                achievementId = achievement.id,
                name = achievement.name,
                description = achievement.description,
                phrase = achievement.phrase,
                grade = achievement.grade,
                category = achievement.category,
                isAchieved = userAchieved != null,
                achievedAt = userAchieved?.achievedAt,
                progress = progressInfo
            )
        }
    }
}
