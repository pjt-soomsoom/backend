package com.soomsoom.backend.adapter.`in`.web.api.activity.request

import com.soomsoom.backend.application.port.`in`.activity.query.SearchActivitiesCriteria
import com.soomsoom.backend.domain.common.DeletionStatus

data class SearchActivitiesRequest(
    val deletionStatus: DeletionStatus?,
)

fun SearchActivitiesRequest.toCriteria(): SearchActivitiesCriteria {
    return SearchActivitiesCriteria(
        deletionStatus = this.deletionStatus ?: DeletionStatus.ACTIVE
    )
}
