package com.soomsoom.backend.application.service.activityhistory.command

import com.soomsoom.backend.application.port.`in`.activityhistory.usecase.command.DeleteUserActivitySummaryUseCase
import com.soomsoom.backend.application.port.out.activityhistory.ActivityHistoryPort
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class DeleteUserActivitySummaryService(
    private val activityHistoryPort: ActivityHistoryPort,
) : DeleteUserActivitySummaryUseCase {

    @PreAuthorize("hasRole('ADMIN') or authentication.principal.id == #userId")
    @Transactional
    override fun deleteByUserId(userId: Long) {
        activityHistoryPort.deleteUserActivitySummaryByUserId(userId)
    }
}
