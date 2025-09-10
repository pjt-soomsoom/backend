package com.soomsoom.backend.application.port.`in`.activity.query

import com.soomsoom.backend.domain.activity.model.enums.ActivityCategory
import com.soomsoom.backend.domain.activity.model.enums.ActivityType
import com.soomsoom.backend.domain.common.DeletionStatus

data class SearchActivitiesCriteria(
    val userId: Long,
    val deletionStatus: DeletionStatus = DeletionStatus.ACTIVE,
    val type: ActivityType?,
    val category: ActivityCategory?,
)
