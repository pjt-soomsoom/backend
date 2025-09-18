package com.soomsoom.backend.application.port.`in`.mailbox.query

import com.soomsoom.backend.domain.common.DeletionStatus
import org.springframework.data.domain.Pageable

/**
 * [관리자] 공지 목록 조회를 위한 검색 조건(Criteria) 객체
 */
data class FindAnnouncementsCriteria(
    val pageable: Pageable,
    val deletionStatus: DeletionStatus = DeletionStatus.ACTIVE,
)
