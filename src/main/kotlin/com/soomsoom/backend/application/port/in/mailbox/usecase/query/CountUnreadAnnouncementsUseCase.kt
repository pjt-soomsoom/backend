package com.soomsoom.backend.application.port.`in`.mailbox.usecase.query

import com.soomsoom.backend.application.port.`in`.mailbox.dto.UnreadAnnouncementsCountDto

/** [사용자] 안 읽은 공지 개수 확인 */
interface CountUnreadAnnouncementsUseCase {
    fun count(userId: Long): UnreadAnnouncementsCountDto
}
