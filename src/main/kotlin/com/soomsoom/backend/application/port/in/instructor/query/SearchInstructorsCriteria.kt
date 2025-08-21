package com.soomsoom.backend.application.port.`in`.instructor.query

import com.soomsoom.backend.domain.common.DeletionStatus

data class SearchInstructorsCriteria(
    val userId: Long,
    val deletionStatus: DeletionStatus = DeletionStatus.ACTIVE,
)
