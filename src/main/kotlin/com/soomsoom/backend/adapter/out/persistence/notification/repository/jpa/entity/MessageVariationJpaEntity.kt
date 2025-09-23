package com.soomsoom.backend.adapter.out.persistence.notification.repository.jpa.entity

import com.soomsoom.backend.common.entity.BaseTimeEntity
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Index
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table

@Entity
@Table(
    name = "message_variations",
    indexes = [
        // 템플릿 ID로 variation을 찾는 쿼리 최적화
        Index(name = "idx_mv_notification_templates_id", columnList = "notification_templates_id")
    ]
)
class MessageVariationJpaEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "notification_templates_id", nullable = false)
    var notificationTemplate: NotificationTemplateJpaEntity,

    @Column(nullable = false, name = "title_template")
    var titleTemplate: String,

    @Column(nullable = false, length = 1024, name = "body_template")
    var bodyTemplate: String,

    var active: Boolean,
) : BaseTimeEntity()
