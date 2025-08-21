package com.soomsoom.backend.application.service.activityhistory.query

import com.soomsoom.backend.application.port.`in`.activityhistory.dto.FindActivityProgressResult
import com.soomsoom.backend.application.port.`in`.activityhistory.usecase.query.FindActivityProgressUseCase
import com.soomsoom.backend.application.port.out.activityhistory.ActivityHistoryPort
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class FindActivityProgressService(
    private val activityHistoryPort: ActivityHistoryPort,
) : FindActivityProgressUseCase {

    @PreAuthorize("hasRole('ADMIN') or #userId == authentication.principal.id")
    override fun find(userId: Long, activityId: Long): FindActivityProgressResult? {
        return activityHistoryPort.findProgress(userId, activityId)?.let {
            FindActivityProgressResult(
                activityId = it.activityId,
                progressSeconds = it.progressSeconds
            )
        }
    }
}
