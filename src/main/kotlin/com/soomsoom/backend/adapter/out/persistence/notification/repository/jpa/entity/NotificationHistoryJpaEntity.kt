package com.soomsoom.backend.adapter.out.persistence.notification.repository.jpa.entity

import com.soomsoom.backend.common.entity.BaseTimeEntity
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Index
import jakarta.persistence.Table
import java.time.LocalDateTime

@Entity
@Table(
    name = "notification_histories",
    indexes = [
        Index(name = "idx_nh_user_sent_at", columnList = "user_id, sent_at DESC")
    ]
)
class NotificationHistoryJpaEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(nullable = false, name = "user_id")
    val userId: Long,

    @Column(nullable = false, name = "message_variations_id")
    val messageVariationId: Long,

    @Column(nullable = false, name = "sent_at")
    val sentAt: LocalDateTime,

    @Column(name = "clicked_at")
    var clickedAt: LocalDateTime? = null,
) : BaseTimeEntity()
