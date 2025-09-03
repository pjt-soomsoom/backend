package com.soomsoom.backend.adapter.`in`.web.api.achievement.request

import com.soomsoom.backend.application.port.`in`.achievement.query.FindMyAchievementsCriteria
import com.soomsoom.backend.domain.achievement.model.AchievementStatusFilter
import com.soomsoom.backend.domain.common.DeletionStatus

data class FindMyAchievementsRequest(
    val userId: Long?,
    val statusFilter: AchievementStatusFilter?,
    val deletionStatus: DeletionStatus?,
)

fun FindMyAchievementsRequest.toCriteria(principalId: Long): FindMyAchievementsCriteria {
    return FindMyAchievementsCriteria(
        userId = this.userId ?: principalId,
        statusFilter = this.statusFilter ?: AchievementStatusFilter.ALL,
        deletionStatus = this.deletionStatus ?: DeletionStatus.ACTIVE
    )
}
