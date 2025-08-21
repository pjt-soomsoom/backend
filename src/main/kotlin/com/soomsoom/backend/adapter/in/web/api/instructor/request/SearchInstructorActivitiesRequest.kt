package com.soomsoom.backend.adapter.`in`.web.api.instructor.request

import com.soomsoom.backend.application.port.`in`.activity.query.SearchInstructorActivitiesCriteria
import com.soomsoom.backend.domain.common.DeletionStatus

data class SearchInstructorActivitiesRequest(
    val userId: Long?,
    val deletionStatus: DeletionStatus?,
)

fun SearchInstructorActivitiesRequest.toCriteria(instructorId: Long, principalId: Long): SearchInstructorActivitiesCriteria {
    return SearchInstructorActivitiesCriteria(
        userId = this.userId ?: principalId,
        instructorId = instructorId,
        deletionStatus = this.deletionStatus ?: DeletionStatus.ACTIVE
    )
}
