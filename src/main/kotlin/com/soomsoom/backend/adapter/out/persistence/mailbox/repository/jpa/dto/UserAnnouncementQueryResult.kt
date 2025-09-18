package com.soomsoom.backend.adapter.out.persistence.mailbox.repository.jpa.dto

import com.querydsl.core.annotations.QueryProjection
import java.time.LocalDateTime

data class UserAnnouncementQueryResult @QueryProjection constructor(
    val userAnnouncementId: Long,
    val announcementId: Long,
    val title: String,
    val receivedAt: LocalDateTime,
    val isRead: Boolean,
)
