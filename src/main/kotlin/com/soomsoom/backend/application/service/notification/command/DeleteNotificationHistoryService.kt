package com.soomsoom.backend.application.service.notification.command

import com.soomsoom.backend.application.port.`in`.notification.usecase.command.DeleteNotificationHistoryUseCase
import com.soomsoom.backend.application.port.out.notification.UserNotificationPort
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class DeleteNotificationHistoryService(
    private val userNotificationPort: UserNotificationPort,
) : DeleteNotificationHistoryUseCase {

    @PreAuthorize("hasRole('ADMIN') or authentication.principal.id == #userId")
    @Transactional
    override fun deleteByUserId(userId: Long) {
        userNotificationPort.deleteNotificationHistoryByUserId(userId)
    }
}
