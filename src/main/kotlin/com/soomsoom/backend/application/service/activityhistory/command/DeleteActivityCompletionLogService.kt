package com.soomsoom.backend.application.service.activityhistory.command

import com.soomsoom.backend.application.port.`in`.activityhistory.usecase.command.DeleteActivityCompletionLogUseCase
import com.soomsoom.backend.application.port.out.activityhistory.ActivityHistoryPort
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class DeleteActivityCompletionLogService(
    private val activityHistoryPort: ActivityHistoryPort,
) : DeleteActivityCompletionLogUseCase {

    @PreAuthorize("hasRole('ADMIN') or authentication.principal.id == #userId")
    @Transactional
    override fun deleteByUserId(userId: Long) {
        activityHistoryPort.deleteActivityCompletionLogByUserId(userId)
    }
}
