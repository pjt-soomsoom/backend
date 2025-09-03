package com.soomsoom.backend.adapter.`in`.web.api.achievement.request

import com.soomsoom.backend.application.port.`in`.achievement.command.UpdateAchievementCommand
import com.soomsoom.backend.domain.achievement.model.AchievementCategory
import com.soomsoom.backend.domain.achievement.model.AchievementGrade
import jakarta.validation.Valid
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull

data class UpdateAchievementRequest(
    @field:NotBlank val name: String,
    @field:NotBlank val description: String,
    val phrase: String?,
    @field:NotNull val grade: AchievementGrade,
    @field:NotNull val category: AchievementCategory,
    val rewardPoints: Int?,
    val rewardItemId: Long?,
    @field:Valid
    val conditions: List<ConditionRequest>?, // Nullable로 변경
) {
    fun toCommand(id: Long): UpdateAchievementCommand {
        return UpdateAchievementCommand(
            id = id,
            name = this.name,
            description = this.description,
            phrase = this.phrase,
            grade = this.grade,
            category = this.category,
            rewardPoints = this.rewardPoints,
            rewardItemId = this.rewardItemId,
            // conditions가 null이 아닐 때만 매핑, null이면 그대로 null 전달
            conditions = this.conditions?.map {
                UpdateAchievementCommand.ConditionCommand(
                    type = it.type,
                    targetValue = it.targetValue
                )
            }
        )
    }
}
