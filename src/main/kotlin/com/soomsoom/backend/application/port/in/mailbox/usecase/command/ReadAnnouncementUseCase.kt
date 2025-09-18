package com.soomsoom.backend.application.port.`in`.mailbox.usecase.command

import com.soomsoom.backend.application.port.`in`.mailbox.dto.AnnouncementDto

/** [사용자] 공지 읽음 처리 및 상세 조회 - DeletionStatus 없음! (삭제된 공지는 조회 불가) */
interface ReadAnnouncementUseCase {
    fun command(userId: Long, userAnnouncementId: Long): AnnouncementDto
}
