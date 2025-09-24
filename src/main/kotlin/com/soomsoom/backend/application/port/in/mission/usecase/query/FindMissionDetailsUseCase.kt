package com.soomsoom.backend.application.port.`in`.mission.usecase.query

import com.soomsoom.backend.application.port.`in`.mission.dto.MissionDetailsDto
import com.soomsoom.backend.domain.common.DeletionStatus

interface FindMissionDetailsUseCase {
    fun findMissionDetails(missionId: Long, deletionsStatus: DeletionStatus = DeletionStatus.ACTIVE): MissionDetailsDto
}
