package com.soomsoom.backend.application.port.`in`.achievement.query

import com.soomsoom.backend.domain.achievement.model.enums.AchievementCategory
import com.soomsoom.backend.domain.achievement.model.enums.AchievementGrade
import com.soomsoom.backend.domain.common.DeletionStatus

data class FindAllAchievementsCriteria(
    val category: AchievementCategory?,
    val grade: AchievementGrade?,
    val deletionStatus: DeletionStatus = DeletionStatus.ACTIVE,
)
