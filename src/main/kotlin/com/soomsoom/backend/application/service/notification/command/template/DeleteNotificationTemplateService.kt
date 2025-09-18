package com.soomsoom.backend.application.service.notification.command.template

import com.soomsoom.backend.application.port.`in`.notification.usecase.command.template.DeleteNotificationTemplateUseCase
import com.soomsoom.backend.application.port.out.notification.NotificationTemplatePort
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class DeleteNotificationTemplateService(
    private val port: NotificationTemplatePort,
) : DeleteNotificationTemplateUseCase {

    @PreAuthorize("hasRole('ADMIN')")
    override fun command(id: Long) {
        port.deleteTemplateById(id)
    }
}
