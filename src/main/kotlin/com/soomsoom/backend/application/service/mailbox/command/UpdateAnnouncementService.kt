package com.soomsoom.backend.application.service.mailbox.command

import com.soomsoom.backend.application.port.`in`.mailbox.command.UpdateAnnouncementCommand
import com.soomsoom.backend.application.port.`in`.mailbox.usecase.command.UpdateAnnouncementUseCase
import com.soomsoom.backend.application.port.out.mailbox.AnnouncementPort
import com.soomsoom.backend.common.exception.SoomSoomException
import com.soomsoom.backend.domain.mailbox.MailboxErrorCode
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class UpdateAnnouncementService(
    private val announcementPort: AnnouncementPort,
) : UpdateAnnouncementUseCase {

    @PreAuthorize("hasRole('ADMIN')")
    override fun command(command: UpdateAnnouncementCommand) {
        val announcement = announcementPort.findById(command.id)
            ?: throw SoomSoomException(MailboxErrorCode.ANNOUNCEMENT_NOT_FOUND)

        announcement.update(
            title = command.title,
            content = command.content
        )

        announcementPort.save(announcement)
    }
}
