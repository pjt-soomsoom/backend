package com.soomsoom.backend.adapter.out.persistence.mailbox.repository.jpa.entity

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
    name = "user_announcements",
    indexes = [
        // userId로 조회 후 receivedAt으로 정렬하는 페이징 쿼리 최적화
        Index(name = "idx_ua_user_received", columnList = "user_id, received_at DESC"),

        // 안 읽은 개수 카운트 쿼리에 완벽하게 최적화된 인덱스
        Index(name = "idx_user_announcements_unread", columnList = "user_id, read, deleted_at"),

        // announcementId로 일괄 삭제하는 쿼리 최적화
        Index(name = "idx_user_announcements_announcement_id", columnList = "announcement_id")
    ]
)
class UserAnnouncementJpaEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0L,

    @Column(name = "user_id")
    val userId: Long,

    @Column(name = "announcement_id")
    val announcementId: Long,
    var read: Boolean,

    @Column(name = "received_at")
    val receivedAt: LocalDateTime,

    @Column(name = "read_at")
    var readAt: LocalDateTime?,
) : BaseTimeEntity()
