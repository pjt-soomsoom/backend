package com.soomsoom.backend.adapter.`in`.web.api.achievement.request

import com.soomsoom.backend.application.port.`in`.achievement.query.FindAllAchievementsCriteria
import com.soomsoom.backend.domain.common.DeletionStatus

data class FindAllAchievementsRequest(
    val deletionStatus: DeletionStatus?,
) {
    fun toCriteria(): FindAllAchievementsCriteria {
        return FindAllAchievementsCriteria(
            deletionStatus = this.deletionStatus ?: DeletionStatus.ACTIVE
        )
    }
}
