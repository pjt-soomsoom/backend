package com.soomsoom.backend.application.service.notification.command.message

import com.soomsoom.backend.application.port.`in`.notification.command.message.UpdateMessageVariationCommand
import com.soomsoom.backend.application.port.`in`.notification.usecase.command.message.UpdateMessageVariationUseCase
import com.soomsoom.backend.application.port.out.notification.NotificationTemplatePort
import com.soomsoom.backend.common.exception.SoomSoomException
import com.soomsoom.backend.domain.notification.NotificationErrorCode
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class UpdateMessageVariationService(
    private val port: NotificationTemplatePort,
) : UpdateMessageVariationUseCase {

    @PreAuthorize("hasRole('ADMIN')")
    override fun command(command: UpdateMessageVariationCommand) {
        val variation = port.findVariationById(command.id)
            ?: throw SoomSoomException(NotificationErrorCode.MESSAGE_VARIATION_NOT_FOUND)

        variation.update(command.titleTemplate, command.bodyTemplate, command.isActive)
        port.saveVariation(variation)
    }
}
