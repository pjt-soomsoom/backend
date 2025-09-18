package com.soomsoom.backend.adapter.out.persistence.notification.repository.jpa

import com.soomsoom.backend.adapter.out.persistence.notification.repository.jpa.entity.NotificationTemplateJpaEntity
import com.soomsoom.backend.domain.notification.model.enums.NotificationType
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

interface NotificationTemplateJpaRepository : JpaRepository<NotificationTemplateJpaEntity, Long> {
    fun findAllByType(type: NotificationType): List<NotificationTemplateJpaEntity>

    @Query("SELECT nt FROM NotificationTemplateJpaEntity nt JOIN FETCH nt.variations WHERE nt.id = :id")
    fun findByIdWithVariations(id: Long): NotificationTemplateJpaEntity?
}
