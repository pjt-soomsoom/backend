package com.soomsoom.backend.application.service.mission.query

import com.soomsoom.backend.application.port.`in`.mission.dto.MissionDetailsDto
import com.soomsoom.backend.application.port.`in`.mission.usecase.query.FindMissionDetailsUseCase
import com.soomsoom.backend.application.port.out.mission.MissionPort
import com.soomsoom.backend.common.exception.SoomSoomException
import com.soomsoom.backend.domain.common.DeletionStatus
import com.soomsoom.backend.domain.mission.MissionErrorCode
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

/**
 * 관리자가 특정 미션의 상세 정보를 조회하는 유스케이스의 구현체
 */
@Service
@Transactional(readOnly = true)
class FindMissionDetailsService(
    private val missionPort: MissionPort,
) : FindMissionDetailsUseCase {

    @PreAuthorize("hasRole('ADMIN')")
    override fun findMissionDetails(missionId: Long, deletionStatus: DeletionStatus): MissionDetailsDto {
        val mission = missionPort.findById(missionId, deletionStatus)
            ?: throw SoomSoomException(MissionErrorCode.NOT_FOUND)

        return MissionDetailsDto.from(mission)
    }
}
