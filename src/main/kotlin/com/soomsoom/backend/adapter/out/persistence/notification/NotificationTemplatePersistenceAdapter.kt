package com.soomsoom.backend.adapter.out.persistence.notification

import com.soomsoom.backend.adapter.out.persistence.notification.repository.jpa.MessageVariationJpaRepository
import com.soomsoom.backend.adapter.out.persistence.notification.repository.jpa.NotificationTemplateJpaRepository
import com.soomsoom.backend.adapter.out.persistence.notification.repository.jpa.NotificationTemplateQueryDslRepository
import com.soomsoom.backend.application.port.out.notification.NotificationTemplatePort
import com.soomsoom.backend.common.exception.SoomSoomException
import com.soomsoom.backend.domain.notification.NotificationErrorCode
import com.soomsoom.backend.domain.notification.model.entity.MessageVariation
import com.soomsoom.backend.domain.notification.model.entity.NotificationTemplate
import com.soomsoom.backend.domain.notification.model.enums.NotificationType
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Component

@Component
class NotificationTemplatePersistenceAdapter(
    private val templateJpaRepository: NotificationTemplateJpaRepository,
    private val variationJpaRepository: MessageVariationJpaRepository,
    private val notificationTemplateQueryDslRepository: NotificationTemplateQueryDslRepository,
) : NotificationTemplatePort{
    override fun saveTemplate(template: NotificationTemplate): NotificationTemplate {
        val entity = template.toEntity()
        return templateJpaRepository.save(entity).toDomain()
    }

    override fun deleteTemplateById(id: Long) {
        templateJpaRepository.deleteById(id)
    }

    override fun findTemplateByIdWithVariations(id: Long): NotificationTemplate? {
        return templateJpaRepository.findByIdWithVariations(id)?.toDomain()
    }

    override fun findAllTemplates(): List<NotificationTemplate> {
        return templateJpaRepository.findAll().map { it.toDomain() }
    }

    override fun findVariationById(id: Long): MessageVariation? {
        return variationJpaRepository.findByIdWithTemplate(id)?.let {
            val templateDomain = it.notificationTemplate.toDomain()
            it.toDomain(templateDomain)
        }
    }

    override fun saveVariation(variation: MessageVariation): MessageVariation {
        val templateEntity = templateJpaRepository.findByIdOrNull(variation.notificationTemplate.id)
            ?: throw SoomSoomException(NotificationErrorCode.TEMPLATE_NOT_FOUND)
        val variationEntity = variation.toEntity(templateEntity)
        return variationJpaRepository.save(variationEntity).toDomain(variation.notificationTemplate)
    }

    override fun findActiveTemplatesWithActiveVariationsByType(type: NotificationType): List<NotificationTemplate> {
        return notificationTemplateQueryDslRepository.findActiveTemplatesWithActiveVariationsByType(type).map { it.toDomain() }
    }
}
