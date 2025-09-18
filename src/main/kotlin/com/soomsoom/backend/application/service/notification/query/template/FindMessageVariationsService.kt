package com.soomsoom.backend.application.service.notification.query.template

import com.soomsoom.backend.application.port.`in`.notification.dto.MessageVariationDto
import com.soomsoom.backend.application.port.`in`.notification.dto.toDto
import com.soomsoom.backend.application.port.`in`.notification.usecase.query.message.FindMessageVariationsUseCase
import com.soomsoom.backend.application.port.out.notification.NotificationTemplatePort
import com.soomsoom.backend.common.exception.SoomSoomException
import com.soomsoom.backend.domain.notification.NotificationErrorCode
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class FindMessageVariationsService(
    private val port: NotificationTemplatePort,
) : FindMessageVariationsUseCase {

    @PreAuthorize("hasRole('ADMIN')")
    override fun findById(id: Long): MessageVariationDto {
        val variation = port.findVariationById(id)
            ?: throw SoomSoomException(NotificationErrorCode.TEMPLATE_NOT_FOUND)
        return variation.toDto()
    }
}
