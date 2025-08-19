package com.soomsoom.backend.adapter.`in`.web.api.instructor.request

import com.soomsoom.backend.application.port.`in`.instructor.query.SearchInstructorsCriteria
import com.soomsoom.backend.domain.common.DeletionStatus

data class SearchInstructorsRequest(
    val deletionStatus: DeletionStatus?,
)

fun SearchInstructorsRequest.toCriteria(): SearchInstructorsCriteria {
    return SearchInstructorsCriteria(
        deletionStatus = this.deletionStatus ?: DeletionStatus.ACTIVE
    )
}
