package com.soomsoom.backend.application.port.`in`.achievement.query

import com.soomsoom.backend.domain.achievement.model.enums.AchievementStatusFilter
import com.soomsoom.backend.domain.common.DeletionStatus

data class FindMyAchievementsCriteria(
    val userId: Long,
    val statusFilter: AchievementStatusFilter = AchievementStatusFilter.ALL,
    val deletionStatus: DeletionStatus = DeletionStatus.ACTIVE,
)
