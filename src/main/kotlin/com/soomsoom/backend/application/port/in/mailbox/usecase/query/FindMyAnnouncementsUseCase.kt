package com.soomsoom.backend.application.port.`in`.mailbox.usecase.query

import com.soomsoom.backend.application.port.`in`.mailbox.dto.UserAnnouncementDto
import org.springframework.data.domain.Pageable

/** [사용자] 내 우편함 조회 - DeletionStatus 없음! (항상 ACTIVE만 조회) */
interface FindMyAnnouncementsUseCase {
    fun find(userId: Long, pageable: Pageable): List<UserAnnouncementDto>
}
