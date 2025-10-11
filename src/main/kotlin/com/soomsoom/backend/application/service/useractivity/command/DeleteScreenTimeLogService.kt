package com.soomsoom.backend.application.service.useractivity.command

import com.soomsoom.backend.application.port.`in`.useractivity.usecase.command.DeleteScreenTimeLogUseCase
import com.soomsoom.backend.application.port.out.useractivity.ScreenTimeLogPort
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.stereotype.Service

@Service
class DeleteScreenTimeLogService(
    private val screenTimeLogPort: ScreenTimeLogPort,
) : DeleteScreenTimeLogUseCase {

    @PreAuthorize("hasRole('ADMIN') or authentication.principal.id == #userId")
    override fun deleteByUserId(userId: Long) {
        screenTimeLogPort.deleteByUserId(userId)
    }
}
