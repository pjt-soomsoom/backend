package com.soomsoom.backend.adapter.`in`.web.api.activity.request

import com.soomsoom.backend.application.port.`in`.activity.query.SearchActivitiesCriteria
import com.soomsoom.backend.domain.activity.model.enums.ActivityCategory
import com.soomsoom.backend.domain.activity.model.enums.ActivityType
import com.soomsoom.backend.domain.common.DeletionStatus

data class SearchActivitiesRequest(
    val userId: Long?,
    val deletionStatus: DeletionStatus?,
    val type: ActivityType?,
    val category: ActivityCategory?,
)

fun SearchActivitiesRequest.toCriteria(principalId: Long): SearchActivitiesCriteria {
    return SearchActivitiesCriteria(
        userId = this.userId ?: principalId,
        deletionStatus = this.deletionStatus ?: DeletionStatus.ACTIVE,
        type = this.type,
        category = this.category
    )
}
