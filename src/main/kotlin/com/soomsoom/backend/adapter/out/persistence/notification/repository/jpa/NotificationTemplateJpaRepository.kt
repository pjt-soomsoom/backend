package com.soomsoom.backend.adapter.out.persistence.notification.repository.jpa

import com.soomsoom.backend.adapter.out.persistence.notification.repository.jpa.entity.NotificationTemplateJpaEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

interface NotificationTemplateJpaRepository : JpaRepository<NotificationTemplateJpaEntity, Long> {
    @Query("SELECT nt FROM NotificationTemplateJpaEntity nt LEFT JOIN FETCH nt.variations WHERE nt.id = :id")
    fun findByIdWithVariations(id: Long): NotificationTemplateJpaEntity?
}
