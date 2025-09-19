package com.soomsoom.backend.application.port.`in`.achievement.command

import com.soomsoom.backend.domain.achievement.model.enums.AchievementCategory
import com.soomsoom.backend.domain.achievement.model.enums.AchievementGrade
import com.soomsoom.backend.domain.achievement.model.enums.ConditionType
import com.soomsoom.backend.domain.achievement.model.vo.AchievementReward
import com.soomsoom.backend.domain.achievement.model.vo.DisplayInfo

data class UpdateAchievementCommand(
    val id: Long,
    val name: String,
    val phrase: String?,
    val grade: AchievementGrade,
    val category: AchievementCategory,
    val unlockedDisplayInfo: DisplayInfo,
    val reward: AchievementReward?,
    val conditions: List<ConditionCommand>?,
) {
    data class ConditionCommand(
        val type: ConditionType,
        val targetValue: Int,
    )
}
