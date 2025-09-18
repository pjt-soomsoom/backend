package com.soomsoom.backend.application.service.notification.query.template

import com.soomsoom.backend.application.port.`in`.notification.dto.NotificationTemplateDto
import com.soomsoom.backend.application.port.`in`.notification.dto.toDto
import com.soomsoom.backend.application.port.`in`.notification.usecase.query.template.FindNotificationTemplatesUseCase
import com.soomsoom.backend.application.port.out.notification.NotificationTemplatePort
import com.soomsoom.backend.common.exception.SoomSoomException
import com.soomsoom.backend.domain.notification.NotificationErrorCode
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class FindNotificationTemplatesService(
    private val port: NotificationTemplatePort,
) : FindNotificationTemplatesUseCase {

    @PreAuthorize("hasRole('ADMIN')")
    override fun findAll(): List<NotificationTemplateDto> {
        return port.findAllTemplates().map { it.toDto() }
    }

    @PreAuthorize("hasRole('ADMIN')")
    override fun findById(id: Long): NotificationTemplateDto {
        val template = port.findTemplateByIdWithVariations(id)
            ?: throw SoomSoomException(NotificationErrorCode.TEMPLATE_NOT_FOUND)
        return template.toDto()
    }
}
