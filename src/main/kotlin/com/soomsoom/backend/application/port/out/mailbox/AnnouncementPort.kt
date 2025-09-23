package com.soomsoom.backend.application.port.out.mailbox

import com.soomsoom.backend.domain.common.DeletionStatus
import com.soomsoom.backend.domain.mailbox.model.Announcement
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface AnnouncementPort {
    fun save(announcement: Announcement): Announcement
    fun findById(id: Long): Announcement?

    /**
     * [관리자] 공지 목록을 조회
     * DeletionStatus에 따라 활성화/삭제된 공지를 필터링 가능
     */
    fun findAll(pageable: Pageable, deletionStatus: DeletionStatus): Page<Announcement>
}
