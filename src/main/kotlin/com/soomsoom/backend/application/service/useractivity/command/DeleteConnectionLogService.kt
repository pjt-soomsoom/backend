package com.soomsoom.backend.application.service.useractivity.command

import com.soomsoom.backend.application.port.`in`.useractivity.usecase.command.DeleteConnectionLogUseCase
import com.soomsoom.backend.application.port.out.useractivity.ConnectionLogPort
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class DeleteConnectionLogService(
    private val connectionLogPort: ConnectionLogPort,
) : DeleteConnectionLogUseCase {

    @PreAuthorize("hasRole('ADMIN') or authentication.principal.id == #userId")
    @Transactional
    override fun deleteByUserId(userId: Long) {
        connectionLogPort.deleteByUserId(userId)
    }
}
