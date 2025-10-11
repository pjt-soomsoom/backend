package com.soomsoom.backend.application.service.mission.command

import com.soomsoom.backend.application.port.`in`.mission.usecase.command.DeleteMissionCompletionLogUseCase
import com.soomsoom.backend.application.port.out.mission.MissionCompletionLogPort
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class DeleteMissionCompletionLogService(
    private val missionCompletionLogPort: MissionCompletionLogPort,
) : DeleteMissionCompletionLogUseCase {

    @PreAuthorize("hasRole('ADMIN') or authentication.principal.id == #userId")
    @Transactional
    override fun deleteByUserId(userId: Long) {
        missionCompletionLogPort.deleteByUserId(userId)
    }
}
