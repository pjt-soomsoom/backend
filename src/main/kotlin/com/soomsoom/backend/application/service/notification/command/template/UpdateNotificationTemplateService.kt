package com.soomsoom.backend.application.service.notification.command.template

import com.soomsoom.backend.application.port.`in`.notification.command.template.UpdateNotificationTemplateCommand
import com.soomsoom.backend.application.port.`in`.notification.usecase.command.template.UpdateNotificationTemplateUseCase
import com.soomsoom.backend.application.port.out.notification.NotificationTemplatePort
import com.soomsoom.backend.common.exception.SoomSoomException
import com.soomsoom.backend.domain.notification.NotificationErrorCode
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class UpdateNotificationTemplateService(
    private val port: NotificationTemplatePort,
) : UpdateNotificationTemplateUseCase {

    @PreAuthorize("hasRole('ADMIN')")
    override fun command(command: UpdateNotificationTemplateCommand) {
        val template = port.findTemplateByIdWithVariations(command.id)
            ?: throw SoomSoomException(NotificationErrorCode.TEMPLATE_NOT_FOUND)

        template.update(command.type, command.description, command.isActive, command.triggerCondition)
        port.saveTemplate(template)
    }
}
