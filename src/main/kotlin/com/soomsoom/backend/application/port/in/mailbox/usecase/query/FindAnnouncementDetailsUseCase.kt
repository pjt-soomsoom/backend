package com.soomsoom.backend.application.port.`in`.mailbox.usecase.query

import com.soomsoom.backend.application.port.`in`.mailbox.dto.AnnouncementDto

/** [관리자] 공지 상세 조회 - 삭제된 공지도 ID로 조회 가능해야 함 */
interface FindAnnouncementDetailsUseCase {
    fun find(id: Long): AnnouncementDto
}
