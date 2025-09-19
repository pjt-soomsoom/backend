package com.soomsoom.backend.adapter.`in`.web.api.achievement.request

import com.soomsoom.backend.application.port.`in`.achievement.command.UpdateAchievementCommand
import com.soomsoom.backend.domain.achievement.model.enums.AchievementCategory
import com.soomsoom.backend.domain.achievement.model.enums.AchievementGrade
import com.soomsoom.backend.domain.achievement.model.vo.AchievementReward
import com.soomsoom.backend.domain.achievement.model.vo.DisplayInfo
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.Valid
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull

@Schema(description = "업적 수정 요청 DTO")
data class UpdateAchievementRequest(
    @field:Schema(description = "업적 이름", example = "성장의 3일")
    @field:NotBlank
    val name: String,

    @field:Schema(description = "업적 달성 시 보여주는 격려 문구", example = "대단해요! 마음의 근육이 자라나고 있어요!")
    val phrase: String?,

    @field:Schema(description = "업적 등급", example = "SILVER")
    @field:NotNull
    val grade: AchievementGrade,

    @field:Schema(description = "업적 카테고리", example = "DIARY")
    @field:NotNull
    val category: AchievementCategory,

    @field:Schema(description = "업적 달성 시 표시될 메시지 정보")
    @field:NotNull
    @field:Valid
    val unlockedDisplayInfo: CreateAchievementRequest.DisplayInfoRequest,

    @field:Schema(description = "업적 보상 정보 (보상이 없으면 null 또는 생략)")
    @field:Valid
    val reward: CreateAchievementRequest.RewardRequest?,

    @field:Schema(description = "업적 달성 조건 목록 (수정하지 않으려면 null 또는 생략)")
    @field:Valid
    val conditions: List<ConditionRequest>?,
) {
    fun toCommand(id: Long): UpdateAchievementCommand {
        return UpdateAchievementCommand(
            id = id,
            name = this.name,
            phrase = this.phrase,
            grade = this.grade,
            category = this.category,
            unlockedDisplayInfo = DisplayInfo(
                titleTemplate = this.unlockedDisplayInfo.titleTemplate,
                bodyTemplate = this.unlockedDisplayInfo.bodyTemplate
            ),
            reward = this.reward?.let {
                AchievementReward(
                    points = it.points,
                    itemId = it.itemId,
                    displayInfo = DisplayInfo(
                        titleTemplate = it.displayInfo.titleTemplate,
                        bodyTemplate = it.displayInfo.bodyTemplate
                    )
                )
            },
            conditions = this.conditions?.map {
                UpdateAchievementCommand.ConditionCommand(
                    type = it.type,
                    targetValue = it.targetValue
                )
            }
        )
    }
}
