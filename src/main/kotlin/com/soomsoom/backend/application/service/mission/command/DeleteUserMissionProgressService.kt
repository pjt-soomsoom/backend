package com.soomsoom.backend.application.service.mission.command

import com.soomsoom.backend.application.port.`in`.mission.usecase.command.DeleteUserMissionProgressUseCase
import com.soomsoom.backend.application.port.out.mission.UserMissionProgressPort
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class DeleteUserMissionProgressService(
    private val userMissionProgressPort: UserMissionProgressPort,
) : DeleteUserMissionProgressUseCase {

    @PreAuthorize("hasRole('ADMIN') or authentication.principal.id == #userId")
    @Transactional
    override fun deleteByUserId(userId: Long) {
        userMissionProgressPort.deleteByUserId(userId)
    }
}
