package com.soomsoom.backend.application.service.mission.command

import com.soomsoom.backend.application.port.`in`.mission.command.UpdateMissionCommand
import com.soomsoom.backend.application.port.`in`.mission.usecase.command.UpdateMissionUseCase
import com.soomsoom.backend.application.port.out.mission.MissionPort
import com.soomsoom.backend.common.exception.SoomSoomException
import com.soomsoom.backend.domain.mission.MissionErrorCode
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class UpdateMissionService(
    private val missionPort: MissionPort,
) : UpdateMissionUseCase {

    @PreAuthorize("hasRole('ADMIN')")
    override fun updateMission(command: UpdateMissionCommand) {
        val mission = missionPort.findById(command.missionId)
            ?: throw SoomSoomException(MissionErrorCode.NOT_FOUND)

        mission.update(
            type = command.type,
            title = command.title,
            description = command.description,
            targetValue = command.targetValue,
            completionNotification = command.completionNotification,
            reward = command.reward,
            repeatableType = command.repeatableType,
            claimType = command.claimType
        )

        missionPort.save(mission)
    }
}
