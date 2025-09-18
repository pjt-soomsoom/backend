package com.soomsoom.backend.application.service.notification.command.message

import com.soomsoom.backend.application.port.`in`.notification.command.message.AddMessageVariationCommand
import com.soomsoom.backend.application.port.`in`.notification.usecase.command.message.AddMessageVariationUseCase
import com.soomsoom.backend.application.port.out.notification.NotificationTemplatePort
import com.soomsoom.backend.common.exception.SoomSoomException
import com.soomsoom.backend.domain.notification.NotificationErrorCode
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class AddMessageVariationService(
    private val port: NotificationTemplatePort,
) : AddMessageVariationUseCase {

    @PreAuthorize("hasRole('ADMIN')")
    override fun command(command: AddMessageVariationCommand): Long {
        // Template을 먼저 조회
        val template = port.findTemplateByIdWithVariations(command.templateId)
            ?: throw SoomSoomException(NotificationErrorCode.TEMPLATE_NOT_FOUND)

        // 도메인 객체의 비즈니스 메서드를 통해 Variation을 추가
        template.addVariation(command.titleTemplate, command.bodyTemplate)

        // 애그리거트 루트인 Template을 저장하면, 하위 Variation도 함께 저장
        val savedTemplate = port.saveTemplate(template)

        return savedTemplate.variations.last().id
    }
}
