package com.soomsoom.backend.application.port.`in`.activity.query

import com.soomsoom.backend.domain.common.DeletionStatus

data class SearchActivitiesCriteria(
    val userId: Long,
    val deletionStatus: DeletionStatus = DeletionStatus.ACTIVE,
)
