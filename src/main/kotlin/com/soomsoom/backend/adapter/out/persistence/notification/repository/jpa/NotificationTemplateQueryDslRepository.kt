package com.soomsoom.backend.adapter.out.persistence.notification.repository.jpa

import com.querydsl.jpa.impl.JPAQueryFactory
import com.soomsoom.backend.adapter.out.persistence.notification.repository.jpa.entity.NotificationTemplateJpaEntity
import com.soomsoom.backend.adapter.out.persistence.notification.repository.jpa.entity.QMessageVariationJpaEntity.messageVariationJpaEntity
import com.soomsoom.backend.adapter.out.persistence.notification.repository.jpa.entity.QNotificationTemplateJpaEntity.notificationTemplateJpaEntity
import com.soomsoom.backend.domain.notification.model.enums.NotificationType
import org.springframework.stereotype.Repository

@Repository
class NotificationTemplateQueryDslRepository(
    private val queryFactory: JPAQueryFactory,
) {
    fun findActiveTemplatesWithActiveVariationsByType(type: NotificationType): List<NotificationTemplateJpaEntity> {
        return queryFactory
            .selectFrom(notificationTemplateJpaEntity)
            .leftJoin(notificationTemplateJpaEntity.variations, messageVariationJpaEntity).fetchJoin()
            .where(
                notificationTemplateJpaEntity.type.eq(type),
                notificationTemplateJpaEntity.isActive.isTrue,
                messageVariationJpaEntity.isActive.isTrue
            )
            .fetch()
    }
}
