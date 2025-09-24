package com.soomsoom.backend.application.port.`in`.mission.usecase.query

import com.soomsoom.backend.application.port.`in`.mission.dto.MissionDetailsDto
import com.soomsoom.backend.application.port.`in`.mission.query.FindMissionsCriteria
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface FindAllMissionsUseCase {
    fun findAllMissions(criteria: FindMissionsCriteria, pageable: Pageable): Page<MissionDetailsDto>
}
