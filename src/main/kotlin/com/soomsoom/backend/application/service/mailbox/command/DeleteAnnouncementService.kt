package com.soomsoom.backend.application.service.mailbox.command

import com.soomsoom.backend.application.port.`in`.mailbox.usecase.command.DeleteAnnouncementUseCase
import com.soomsoom.backend.application.port.out.mailbox.AnnouncementPort
import com.soomsoom.backend.common.event.Event
import com.soomsoom.backend.common.event.EventType
import com.soomsoom.backend.common.event.payload.AnnouncementDeletedPayload
import com.soomsoom.backend.common.exception.SoomSoomException
import com.soomsoom.backend.domain.mailbox.MailboxErrorCode
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class DeleteAnnouncementService(
    private val announcementPort: AnnouncementPort,
    private val eventPublisher: ApplicationEventPublisher,
) : DeleteAnnouncementUseCase {
    override fun command(id: Long) {
        val announcement = announcementPort.findById(id)
            ?: throw SoomSoomException(MailboxErrorCode.ANNOUNCEMENT_NOT_FOUND)

        announcement.delete()
        announcementPort.save(announcement)

        val eventPayload = AnnouncementDeletedPayload(announcementId = announcement.id)
        eventPublisher.publishEvent(
            Event(EventType.ANNOUNCEMENT_DELETED, eventPayload)
        )
    }
}
