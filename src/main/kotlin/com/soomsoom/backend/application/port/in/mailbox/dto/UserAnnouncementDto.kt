package com.soomsoom.backend.application.port.`in`.mailbox.dto

import java.time.LocalDateTime

/**
* [사용자] 자신의 우편함(공지 목록)을 조회할 때 사용하는 Application DTO
*/
data class UserAnnouncementDto(
    val userAnnouncementId: Long,
    val announcementId: Long,
    val title: String,
    val receivedAt: LocalDateTime,
    val isRead: Boolean,
)
