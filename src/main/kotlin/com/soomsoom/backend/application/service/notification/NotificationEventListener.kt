package com.soomsoom.backend.application.service.notification

import com.soomsoom.backend.application.port.`in`.notification.command.CreateNotificationSettingCommand
import com.soomsoom.backend.application.port.`in`.notification.usecase.command.CreateNotificationSettingUseCase
import com.soomsoom.backend.application.service.notification.strategy.NotificationStrategy
import com.soomsoom.backend.common.event.Event
import com.soomsoom.backend.common.event.NotificationPayload
import com.soomsoom.backend.common.event.payload.UserCreatedPayload
import org.springframework.core.annotation.Order
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional
import org.springframework.transaction.event.TransactionPhase
import org.springframework.transaction.event.TransactionalEventListener

@Component
class NotificationEventListener(
    private val strategies: List<NotificationStrategy<*>>,
    private val createdNotificationSettingUseCase: CreateNotificationSettingUseCase,
) {
    @Async("threadPoolTaskExecutor")
    @TransactionalEventListener(
        phase = TransactionPhase.AFTER_COMMIT
    )
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @Order(10)
    fun handleEvent(event: Event<*>) {
        if (event.payload is NotificationPayload) {
            val supportedStrategies = strategies.filter { it.supports(event) }
            supportedStrategies.forEach { strategy ->
                @Suppress("UNCHECKED_CAST")
                (strategy as NotificationStrategy<NotificationPayload>).execute(event as Event<NotificationPayload>)
            }
        }
    }

    @Async("threadPoolTaskExecutor")
    @TransactionalEventListener(
        classes = [Event::class],
        condition = "#event.eventType == T(com.soomsoom.backend.common.event.EventType).USER_CREATED",
        phase = TransactionPhase.AFTER_COMMIT
    )
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    fun handleUserCreatedEvent(event: Event<UserCreatedPayload>) {
        val payload = event.payload
        val command = CreateNotificationSettingCommand(
            userId = payload.userId,
            diaryNotificationEnabled = true,
            soomsoomNewsNotificationEnabled = true,
            reEngagementNotificationEnabled = true
        )
        createdNotificationSettingUseCase.create(command)
    }
}
