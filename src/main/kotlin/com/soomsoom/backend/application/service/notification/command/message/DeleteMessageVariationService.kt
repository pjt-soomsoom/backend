package com.soomsoom.backend.application.service.notification.command.message

import com.soomsoom.backend.application.port.`in`.notification.usecase.command.message.DeleteMessageVariationUseCase
import com.soomsoom.backend.application.port.out.notification.NotificationTemplatePort
import com.soomsoom.backend.common.exception.SoomSoomException
import com.soomsoom.backend.domain.notification.NotificationErrorCode
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class DeleteMessageVariationService(
    private val port: NotificationTemplatePort,
) : DeleteMessageVariationUseCase {

    @PreAuthorize("hasRole('ADMIN')")
    override fun command(messageId: Long) {
        // Variation을 찾아 부모 Template의 ID를 알아냄
        val variation = port.findVariationById(messageId)
            ?: throw SoomSoomException(NotificationErrorCode.TEMPLATE_NOT_FOUND)

        // 부모 Template을 조회
        val template = port.findTemplateByIdWithVariations(variation.notificationTemplate.id)
            ?: throw SoomSoomException(NotificationErrorCode.TEMPLATE_NOT_FOUND)

        // 부모의 도메인 메서드를 통해 Variation을 제거합니다.
        template.removeVariation(messageId)

        // 부모를 저장하면 orphanRemoval 설정에 의해 DB에서 자식이 삭제
        port.saveTemplate(template)
    }
}
