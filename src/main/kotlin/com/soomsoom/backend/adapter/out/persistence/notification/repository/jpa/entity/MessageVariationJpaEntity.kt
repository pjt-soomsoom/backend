package com.soomsoom.backend.adapter.out.persistence.notification.repository.jpa.entity

import com.soomsoom.backend.common.entity.BaseTimeEntity
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table

@Entity
@Table(name = "message_variations")
class MessageVariationJpaEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "notification_templates_id", nullable = false)
    var notificationTemplate: NotificationTemplateJpaEntity,

    @Column(nullable = false)
    var titleTemplate: String,

    @Column(nullable = false, length = 1024)
    var bodyTemplate: String,

    var isActive: Boolean,
) : BaseTimeEntity()
