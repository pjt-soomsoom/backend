package com.soomsoom.backend.application.service.mailbox.query

import com.soomsoom.backend.application.port.`in`.mailbox.dto.UnreadAnnouncementsCountDto
import com.soomsoom.backend.application.port.`in`.mailbox.usecase.query.CountUnreadAnnouncementsUseCase
import com.soomsoom.backend.application.port.out.mailbox.UserAnnouncementPort
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class CountUnreadAnnouncementsService(
    private val userAnnouncementPort: UserAnnouncementPort,
) : CountUnreadAnnouncementsUseCase {

    override fun count(userId: Long): UnreadAnnouncementsCountDto {
        val unreadCount = userAnnouncementPort.countUnreadByUserId(userId)
        return UnreadAnnouncementsCountDto(unreadCount = unreadCount)
    }
}
