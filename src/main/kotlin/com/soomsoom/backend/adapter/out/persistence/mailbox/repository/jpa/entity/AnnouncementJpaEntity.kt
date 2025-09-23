package com.soomsoom.backend.adapter.out.persistence.mailbox.repository.jpa.entity

import com.soomsoom.backend.common.entity.BaseTimeEntity
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Index
import jakarta.persistence.Lob
import jakarta.persistence.Table
import java.time.LocalDateTime

@Entity
@Table(
    name = "announcements",
    indexes = [
        Index(name = "idx_announcements_deleted_at", columnList = "deleted_at")
    ]
)
class AnnouncementJpaEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0L,
    var title: String,

    @Lob
    var content: String,

    @Column(name = "sent_at")
    val sentAt: LocalDateTime,
) : BaseTimeEntity()
