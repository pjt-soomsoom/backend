package com.soomsoom.backend.application.port.out.mailbox

import com.soomsoom.backend.application.port.`in`.mailbox.dto.UserAnnouncementDto
import com.soomsoom.backend.domain.mailbox.model.UserAnnouncement
import org.springframework.data.domain.Pageable

interface UserAnnouncementPort {
    fun save(userAnnouncement: UserAnnouncement): UserAnnouncement
    fun saveAll(userAnnouncements: List<UserAnnouncement>): List<UserAnnouncement>
    fun findById(id: Long): UserAnnouncement?
    fun countUnreadByUserId(userId: Long): Int
    fun findDtosByUserId(userId: Long, pageable: Pageable): List<UserAnnouncementDto>

    /**
     * [데이터 정합성] 특정 원본 공지(Announcement) ID와 연결된 모든 UserAnnouncement를 삭제(soft-delete) 처리
     */
    fun deleteAllByAnnouncementId(announcementId: Long)
}
