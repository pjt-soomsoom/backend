package com.soomsoom.backend.application.port.`in`.activity.query

import com.soomsoom.backend.domain.common.DeletionStatus

data class SearchActivitiesCriteria(
    val deletionStatus: DeletionStatus = DeletionStatus.ACTIVE,
)
