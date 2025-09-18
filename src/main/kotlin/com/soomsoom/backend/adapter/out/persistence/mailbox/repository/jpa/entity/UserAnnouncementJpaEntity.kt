package com.soomsoom.backend.adapter.out.persistence.mailbox.repository.jpa.entity

import com.soomsoom.backend.common.entity.BaseTimeEntity
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Index
import jakarta.persistence.Table
import java.time.LocalDateTime

@Entity
@Table(
    name = "user_announcements",
    indexes = [
        Index(name = "idx_user_announcements_user_id", columnList = "userId"),
        Index(name = "idx_user_announcements_unread", columnList = "userId, isRead, deletedAt"),
        Index(name = "idx_user_announcements_announcement_id", columnList = "announcementId")
    ]
)
class UserAnnouncementJpaEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0L,
    val userId: Long,
    val announcementId: Long,
    var isRead: Boolean,
    val receivedAt: LocalDateTime,
    var readAt: LocalDateTime?,
) : BaseTimeEntity()
