package com.soomsoom.backend.application.service.mailbox.command

import com.soomsoom.backend.application.port.`in`.mailbox.dto.AnnouncementDto
import com.soomsoom.backend.application.port.`in`.mailbox.dto.toDto
import com.soomsoom.backend.application.port.`in`.mailbox.usecase.command.ReadAnnouncementUseCase
import com.soomsoom.backend.application.port.out.mailbox.AnnouncementPort
import com.soomsoom.backend.application.port.out.mailbox.UserAnnouncementPort
import com.soomsoom.backend.common.exception.SoomSoomException
import com.soomsoom.backend.domain.mailbox.MailboxErrorCode
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class ReadAnnouncementService(
    private val userAnnouncementPort: UserAnnouncementPort,
    private val announcementPort: AnnouncementPort,
) : ReadAnnouncementUseCase {

    @PreAuthorize("#userId == authentication.principal.id")
    override fun command(userId: Long, userAnnouncementId: Long): AnnouncementDto {
        val userAnnouncement = userAnnouncementPort.findById(userAnnouncementId)
            ?: throw SoomSoomException(MailboxErrorCode.USER_ANNOUNCEMENT_NOT_FOUND)

        if (userAnnouncement.userId != userId) {
            throw SoomSoomException(MailboxErrorCode.USER_ANNOUNCEMENT_NOT_FOUND)
        }

        userAnnouncement.markAsRead()
        userAnnouncementPort.save(userAnnouncement)

        val announcement = announcementPort.findById(userAnnouncement.announcementId)
            ?: throw SoomSoomException(MailboxErrorCode.ANNOUNCEMENT_NOT_FOUND)

        return announcement.toDto()
    }
}
