package com.soomsoom.backend.application.service.notification.command.template

import com.soomsoom.backend.application.port.`in`.notification.command.template.CreateNotificationTemplateCommand
import com.soomsoom.backend.application.port.`in`.notification.usecase.command.template.CreateNotificationTemplateUseCase
import com.soomsoom.backend.application.port.out.notification.NotificationTemplatePort
import com.soomsoom.backend.domain.notification.model.entity.NotificationTemplate
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class CreateNotificationTemplateService(
    private val port: NotificationTemplatePort,
) : CreateNotificationTemplateUseCase {

    @PreAuthorize("hasRole('ADMIN')")
    override fun command(command: CreateNotificationTemplateCommand): Long {
        val newTemplate = NotificationTemplate(
            type = command.type,
            description = command.description,
            isActive = true,
            triggerCondition = command.triggerCondition
        )
        return port.saveTemplate(newTemplate).id
    }
}
