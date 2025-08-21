package com.soomsoom.backend.application.port.`in`.activity.query

import com.soomsoom.backend.domain.common.DeletionStatus

data class SearchInstructorActivitiesCriteria(
    val instructorId: Long,
    val userId: Long?,
    val deletionStatus: DeletionStatus = DeletionStatus.ACTIVE,
)
