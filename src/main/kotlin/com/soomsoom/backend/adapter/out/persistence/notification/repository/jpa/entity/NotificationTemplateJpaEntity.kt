package com.soomsoom.backend.adapter.out.persistence.notification.repository.jpa.entity

import com.soomsoom.backend.common.entity.BaseTimeEntity
import com.soomsoom.backend.domain.notification.model.enums.NotificationType
import jakarta.persistence.CascadeType
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.OneToMany
import jakarta.persistence.Table

@Entity
@Table(name = "notification_templates")
class NotificationTemplateJpaEntity(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0L,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    val type: NotificationType,

    var description: String,

    var isActive: Boolean,

    val triggerCondition: Int? = null,

    @OneToMany(mappedBy = "notificationTemplate", cascade = [CascadeType.ALL], orphanRemoval = true, fetch = FetchType.EAGER)
    val variations: MutableList<MessageVariationJpaEntity> = mutableListOf(),
) : BaseTimeEntity()
