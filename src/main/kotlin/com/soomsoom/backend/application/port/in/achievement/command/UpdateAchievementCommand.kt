package com.soomsoom.backend.application.port.`in`.achievement.command

import com.soomsoom.backend.application.port.`in`.achievement.command.CreateAchievementCommand.ConditionCommand
import com.soomsoom.backend.domain.achievement.model.AchievementCategory
import com.soomsoom.backend.domain.achievement.model.AchievementGrade
import com.soomsoom.backend.domain.achievement.model.ConditionType

data class UpdateAchievementCommand(
    val id: Long,
    val name: String,
    val description: String,
    val phrase: String?,
    val grade: AchievementGrade,
    val category: AchievementCategory,
    val rewardPoints: Int?,
    val rewardItemId: Long?,
    val conditions: List<ConditionCommand>?,
) {
    data class ConditionCommand(
        val type: ConditionType,
        val targetValue: Int,
    )
}
