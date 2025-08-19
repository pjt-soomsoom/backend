package com.soomsoom.backend.application.port.`in`.activity.usecase.query

import com.soomsoom.backend.application.port.`in`.activity.dto.ActivityResult
import com.soomsoom.backend.domain.common.DeletionStatus

interface FindActivityUseCase {
    fun findActivity(activityId: Long, deletionStatus: DeletionStatus = DeletionStatus.ACTIVE): ActivityResult
}
