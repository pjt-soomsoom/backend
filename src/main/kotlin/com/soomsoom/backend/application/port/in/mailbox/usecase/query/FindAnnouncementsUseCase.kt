package com.soomsoom.backend.application.port.`in`.mailbox.usecase.query

import com.soomsoom.backend.application.port.`in`.mailbox.dto.AnnouncementDto
import com.soomsoom.backend.application.port.`in`.mailbox.query.FindAnnouncementsCriteria
import org.springframework.data.domain.Page

/** [관리자] 공지 목록 조회 - DeletionStatus로 필터링 가능 */
interface FindAnnouncementsUseCase {
    fun find(criteria: FindAnnouncementsCriteria): Page<AnnouncementDto>
}
