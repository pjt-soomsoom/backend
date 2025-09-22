package com.soomsoom.backend.application.service.activityhistory.query

import com.soomsoom.backend.application.port.`in`.activityhistory.dto.FindActivityProgressResult
import com.soomsoom.backend.application.port.`in`.activityhistory.usecase.query.FindActivityProgressUseCase
import com.soomsoom.backend.application.port.out.activityhistory.ActivityHistoryPort
import com.soomsoom.backend.domain.activityhistory.model.ActivityProgress
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class FindActivityProgressService(
    private val activityHistoryPort: ActivityHistoryPort,
) : FindActivityProgressUseCase {

    @PreAuthorize("hasRole('ADMIN') or #userId == authentication.principal.id")
    override fun find(userId: Long, activityId: Long): FindActivityProgressResult {
        val activityProgress = (
            activityHistoryPort.findProgress(userId, activityId)
                ?: run {
                    val saveProgress = activityHistoryPort.saveProgress(
                        ActivityProgress(
                            id = null,
                            userId = userId,
                            activityId = activityId,
                            progressSeconds = 0,
                            createdAt = null,
                            modifiedAt = null
                        )
                    )
                    saveProgress
                }
            )

        return FindActivityProgressResult(
            activityId = activityProgress.activityId,
            progressSeconds = activityProgress.progressSeconds
        )
    }
}
