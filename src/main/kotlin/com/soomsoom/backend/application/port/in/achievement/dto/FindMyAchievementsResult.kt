package com.soomsoom.backend.application.port.`in`.achievement.dto

import com.soomsoom.backend.application.helper.ProgressInfo
import com.soomsoom.backend.domain.achievement.model.aggregate.Achievement
import com.soomsoom.backend.domain.achievement.model.entity.UserAchieved
import com.soomsoom.backend.domain.achievement.model.enums.AchievementCategory
import com.soomsoom.backend.domain.achievement.model.enums.AchievementGrade
import io.swagger.v3.oas.annotations.media.Schema
import java.time.LocalDateTime

@Schema(description = "내 업적 조회 결과 DTO (사용자용)")
data class FindMyAchievementsResult(
    @field:Schema(description = "업적 ID") val achievementId: Long,
    @field:Schema(description = "업적 이름") val name: String,
    @field:Schema(description = "업적 상세 설명 (동적으로 생성됨)", example = "3일 연속 일기 작성") val description: String,
    @field:Schema(description = "격려 문구") val phrase: String?,
    @field:Schema(description = "업적 등급") val grade: AchievementGrade,
    @field:Schema(description = "업적 카테고리") val category: AchievementCategory,
    @field:Schema(description = "업적 달성 여부") val isAchieved: Boolean,
    @field:Schema(description = "업적 달성 일시") val achievedAt: LocalDateTime?,
    @field:Schema(description = "업적 진행도 정보") val progress: ProgressInfo?,
) {
    companion object {
        fun of(
            achievement: Achievement,
            userAchieved: UserAchieved?,
            progressInfo: ProgressInfo,
            description: String,
        ): FindMyAchievementsResult {
            return FindMyAchievementsResult(
                achievementId = achievement.id,
                name = achievement.name,
                description = description,
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
