package com.soomsoom.backend.application.port.`in`.achievement.query

import com.soomsoom.backend.domain.common.DeletionStatus

data class FindAllAchievementsCriteria(
    val deletionStatus: DeletionStatus = DeletionStatus.ACTIVE,
)
