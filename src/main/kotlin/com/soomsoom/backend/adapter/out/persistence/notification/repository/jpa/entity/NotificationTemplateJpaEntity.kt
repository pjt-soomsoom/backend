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
import jakarta.persistence.Index
import jakarta.persistence.OneToMany
import jakarta.persistence.Table

@Entity
@Table(
    name = "notification_templates",
    indexes = [
        // 특정 타입의 활성화된 템플릿을 찾는 쿼리 최적화
        Index(name = "idx_nt_type_active", columnList = "type, active")
    ]
)
class NotificationTemplateJpaEntity(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0L,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    val type: NotificationType,

    var description: String,

    var active: Boolean,

    @Column(name = "trigger_condition")
    val triggerCondition: Int? = null,

    @OneToMany(mappedBy = "notificationTemplate", cascade = [CascadeType.ALL], orphanRemoval = true, fetch = FetchType.LAZY)
    val variations: MutableList<MessageVariationJpaEntity> = mutableListOf(),
) : BaseTimeEntity()
