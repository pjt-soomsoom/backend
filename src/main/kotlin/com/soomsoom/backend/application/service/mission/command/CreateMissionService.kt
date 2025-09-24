package com.soomsoom.backend.application.service.mission.command

import com.soomsoom.backend.application.port.`in`.mission.command.CreateMissionCommand
import com.soomsoom.backend.application.port.`in`.mission.usecase.command.CreateMissionUseCase
import com.soomsoom.backend.application.port.out.mission.MissionPort
import com.soomsoom.backend.domain.mission.model.aggregate.Mission
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class CreateMissionService(
    private val missionPort: MissionPort,
) : CreateMissionUseCase {

    @PreAuthorize("hasRole('ADMIN')")
    override fun createMission(command: CreateMissionCommand): Long {
        val newMission = Mission(
            id = 0,
            type = command.type,
            title = command.title,
            description = command.description,
            targetValue = command.targetValue,
            completionNotification = command.completionNotification,
            reward = command.reward,
            repeatableType = command.repeatableType,
            claimType = command.claimType
        )

        val savedMission = missionPort.save(newMission)

        return savedMission.id
    }
}
