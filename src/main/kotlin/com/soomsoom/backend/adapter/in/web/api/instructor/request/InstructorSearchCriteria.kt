package com.soomsoom.backend.adapter.`in`.web.api.instructor.request

import com.soomsoom.backend.adapter.`in`.web.api.common.DeletionStatus

data class InstructorSearchCriteria(
    val deletionStatus: DeletionStatus = DeletionStatus.ACTIVE,
)
