package com.soomsoom.backend.application.service.mailbox

import com.soomsoom.backend.application.port.`in`.mailbox.usecase.command.DeleteUserAnnouncementsUseCase
import com.soomsoom.backend.application.port.`in`.mailbox.usecase.command.DistributeAnnouncementUseCase
import com.soomsoom.backend.common.event.Event
import com.soomsoom.backend.common.event.payload.AnnouncementCreatedNotificationPayload
import com.soomsoom.backend.common.event.payload.AnnouncementDeletedPayload
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Component
import org.springframework.transaction.event.TransactionPhase
import org.springframework.transaction.event.TransactionalEventListener

@Component
class MailboxEventListener(
    private val distributeAnnouncementUseCase: DistributeAnnouncementUseCase,
    private val deleteUserAnnouncementsUseCase: DeleteUserAnnouncementsUseCase,
) {
    @Async("threadPoolTaskExecutor")
    @TransactionalEventListener(
        classes = [Event::class],
        condition = "#event.eventType == T(com.soomsoom.backend.common.event.EventType).ANNOUNCEMENT_CREATED",
        phase = TransactionPhase.AFTER_COMMIT
    )
    fun handleAnnouncementCreatedEvent(event: Event<AnnouncementCreatedNotificationPayload>) {
        distributeAnnouncementUseCase.command(event.payload.announcementId)
    }

    @Async("threadPoolTaskExecutor")
    @TransactionalEventListener(
        classes = [Event::class],
        condition = "#event.eventType == T(com.soomsoom.backend.common.event.EventType).ANNOUNCEMENT_DELETED",
        phase = TransactionPhase.AFTER_COMMIT
    )
    fun handleAnnouncementDeletedEvent(event: Event<AnnouncementDeletedPayload>) {
        deleteUserAnnouncementsUseCase.command(event.payload.announcementId)
    }
}
