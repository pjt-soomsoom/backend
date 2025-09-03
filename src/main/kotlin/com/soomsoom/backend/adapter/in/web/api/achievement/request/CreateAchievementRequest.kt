package com.soomsoom.backend.adapter.`in`.web.api.achievement.request

import com.soomsoom.backend.application.port.`in`.achievement.command.CreateAchievementCommand
import com.soomsoom.backend.domain.achievement.model.AchievementCategory
import com.soomsoom.backend.domain.achievement.model.AchievementGrade
import jakarta.validation.Valid
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotEmpty
import jakarta.validation.constraints.NotNull

data class CreateAchievementRequest(
    @field:NotBlank
    val name: String,
    @field:NotBlank
    val description: String,
    val phrase: String?,
    @field:NotNull
    val grade: AchievementGrade,
    @field:NotNull
    val category: AchievementCategory,
    val rewardPoints: Int?,
    val rewardItemId: Long?,

    @field:NotEmpty
    @field:Valid
    val conditions: List<ConditionRequest>,
) {
    fun toCommand(): CreateAchievementCommand {
        return CreateAchievementCommand(
            name = this.name,
            description = this.description,
            phrase = this.phrase,
            grade = this.grade,
            category = this.category,
            rewardPoints = this.rewardPoints,
            rewardItemId = this.rewardItemId,
            conditions = this.conditions.map {
                CreateAchievementCommand.ConditionCommand(
                    type = it.type,
                    targetValue = it.targetValue
                )
            }
        )
    }
}
