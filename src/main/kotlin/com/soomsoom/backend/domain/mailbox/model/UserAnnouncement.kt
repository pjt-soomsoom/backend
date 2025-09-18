package com.soomsoom.backend.domain.mailbox.model

import java.time.LocalDateTime

class UserAnnouncement(
    val id: Long = 0L,
    val userId: Long,
    val announcementId: Long,
    var isRead: Boolean = false,
    val receivedAt: LocalDateTime,
    var readAt: LocalDateTime? = null,

    val createdAt: LocalDateTime? = null,
    var modifiedAt: LocalDateTime? = null,
    var deletedAt: LocalDateTime? = null,
) {

    /**
     * 공지를 '읽음' 상태로 변경
     */
    fun markAsRead() {
        if (!isRead) {
            this.isRead = true
            this.readAt = LocalDateTime.now()
        }
    }

    /**
     * 원본 공지가 삭제될 때, 이 공지를 함께 삭제(soft-delete) 처리하는 로직
     */
    fun delete() {
        if (this.deletedAt == null) {
            this.deletedAt = LocalDateTime.now()
        }
    }
}
