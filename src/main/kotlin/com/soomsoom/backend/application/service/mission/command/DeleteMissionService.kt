package com.soomsoom.backend.application.service.mission.command

import com.soomsoom.backend.application.port.`in`.mission.usecase.command.DeleteMissionUseCase
import com.soomsoom.backend.application.port.out.mission.MissionPort
import com.soomsoom.backend.common.exception.SoomSoomException
import com.soomsoom.backend.domain.mission.MissionErrorCode
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Transactional
@Service
class DeleteMissionService(
    private val missionPort: MissionPort,
) : DeleteMissionUseCase {

    @PreAuthorize("hasRole('ADMIN')")
    override fun deleteMission(missionId: Long) {
        val mission = missionPort.findById(missionId)
            ?: throw SoomSoomException(MissionErrorCode.NOT_FOUND)

        mission.softDelete()

        missionPort.save(mission)
    }
}
