package com.soomsoom.backend.application.port.`in`.achievement.dto

import com.soomsoom.backend.domain.achievement.model.aggregate.Achievement
import com.soomsoom.backend.domain.achievement.model.enums.AchievementCategory
import com.soomsoom.backend.domain.achievement.model.enums.AchievementGrade
import com.soomsoom.backend.domain.achievement.model.vo.AchievementReward
import com.soomsoom.backend.domain.achievement.model.vo.DisplayInfo
import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "업적 상세 정보 DTO (관리자용)")
data class AchievementDto(
    @field:Schema(description = "업적 ID") val id: Long,
    @field:Schema(description = "업적 이름") val name: String,
    @field:Schema(description = "격려 문구") val phrase: String?,
    @field:Schema(description = "업적 등급") val grade: AchievementGrade,
    @field:Schema(description = "업적 카테고리") val category: AchievementCategory,
    @field:Schema(description = "업적 달성 시 메시지 정보") val unlockedDisplayInfo: DisplayInfo,
    @field:Schema(description = "업적 보상 정보") val reward: AchievementReward?,
    @field:Schema(description = "업적 조건 목록") val conditions: List<AchievementConditionDto>,
) {
    companion object {
        fun from(achievement: Achievement): AchievementDto {
            return AchievementDto(
                id = achievement.id,
                name = achievement.name,
                phrase = achievement.phrase,
                grade = achievement.grade,
                category = achievement.category,
                unlockedDisplayInfo = achievement.unlockedDisplayInfo,
                reward = achievement.reward,
                conditions = achievement.conditions.map { AchievementConditionDto.from(it) }
            )
        }
    }
}
