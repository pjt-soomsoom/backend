package com.soomsoom.backend.application.service.mission.query

import com.soomsoom.backend.application.port.`in`.mission.dto.MissionDetailsDto
import com.soomsoom.backend.application.port.`in`.mission.query.FindMissionsCriteria
import com.soomsoom.backend.application.port.`in`.mission.usecase.query.FindAllMissionsUseCase
import com.soomsoom.backend.application.port.out.mission.MissionPort
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

/**
 * 관리자가 미션 목록을 조회하는 유스케이스의 구현체
 */
@Service
@Transactional(readOnly = true)
class FindAllMissionsService(
    private val missionPort: MissionPort,
) : FindAllMissionsUseCase {

    @PreAuthorize("hasRole('ADMIN')")
    override fun findAllMissions(criteria: FindMissionsCriteria, pageable: Pageable): Page<MissionDetailsDto> {
        val missionPage = missionPort.findAll(criteria, pageable)

        return missionPage.map { mission ->
            MissionDetailsDto.from(mission)
        }
    }
}
