package com.soomsoom.backend.application.service.mailbox.command

import com.soomsoom.backend.application.port.`in`.mailbox.command.CreateAnnouncementCommand
import com.soomsoom.backend.application.port.`in`.mailbox.usecase.command.CreateAnnouncementUseCase
import com.soomsoom.backend.application.port.out.mailbox.AnnouncementPort
import com.soomsoom.backend.common.event.Event
import com.soomsoom.backend.common.event.EventType
import com.soomsoom.backend.common.event.payload.AnnouncementCreatedNotificationPayload
import com.soomsoom.backend.domain.mailbox.model.Announcement
import org.springframework.context.ApplicationEventPublisher
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Service
@Transactional
class CreateAnnouncementService(
    private val announcementPort: AnnouncementPort,
    private val eventPublisher: ApplicationEventPublisher,
) : CreateAnnouncementUseCase {

    @PreAuthorize("hasRole('ADMIN')")
    override fun command(command: CreateAnnouncementCommand): Long {
        val newAnnouncement = Announcement(
            title = command.title,
            content = command.content,
            sentAt = LocalDateTime.now()
        )

        val savedAnnouncement = announcementPort.save(newAnnouncement)

        val eventPayload = AnnouncementCreatedNotificationPayload(
            announcementId = savedAnnouncement.id,
            title = savedAnnouncement.title
        )

        eventPublisher.publishEvent(
            Event(
                eventType = EventType.ANNOUNCEMENT_CREATED,
                payload = eventPayload
            )
        )

        return savedAnnouncement.id
    }
}
