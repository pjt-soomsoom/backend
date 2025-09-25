package com.soomsoom.backend.application.port.`in`.mission.query

import com.soomsoom.backend.domain.common.DeletionStatus

data class FindMissionsCriteria(
    val deletionStatus: DeletionStatus = DeletionStatus.ACTIVE,
)
