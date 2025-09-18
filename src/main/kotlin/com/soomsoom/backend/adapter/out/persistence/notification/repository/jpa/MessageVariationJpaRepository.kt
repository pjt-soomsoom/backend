package com.soomsoom.backend.adapter.out.persistence.notification.repository.jpa

import com.soomsoom.backend.adapter.out.persistence.notification.repository.jpa.entity.MessageVariationJpaEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

interface MessageVariationJpaRepository : JpaRepository<MessageVariationJpaEntity, Long> {
    @Query("SELECT mv FROM MessageVariationJpaEntity mv JOIN FETCH mv.notificationTemplate WHERE mv.id = :id")
    fun findByIdWithTemplate(id: Long): MessageVariationJpaEntity?
}
