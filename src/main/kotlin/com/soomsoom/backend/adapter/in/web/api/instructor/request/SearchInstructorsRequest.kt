package com.soomsoom.backend.adapter.`in`.web.api.instructor.request

import com.soomsoom.backend.application.port.`in`.instructor.query.SearchInstructorsCriteria
import com.soomsoom.backend.domain.common.DeletionStatus

data class SearchInstructorsRequest(
    val userId: Long?,
    val deletionStatus: DeletionStatus?,
)

fun SearchInstructorsRequest.toCriteria(principalId: Long): SearchInstructorsCriteria {
    return SearchInstructorsCriteria(
        userId = this.userId ?: principalId,
        deletionStatus = this.deletionStatus ?: DeletionStatus.ACTIVE
    )
}
