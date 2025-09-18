package com.soomsoom.backend.application.service.notification

import com.soomsoom.backend.application.service.notification.strategy.NotificationStrategy
import com.soomsoom.backend.common.event.Event
import com.soomsoom.backend.common.event.NotificationPayload
import org.springframework.context.event.EventListener
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Component

@Component
class NotificationEventListener(
    private val strategies: List<NotificationStrategy<*>>,
) {
    @Async
    @EventListener
    fun handleEvent(event: Event<*>) {
        if (event.payload is NotificationPayload) {
            val supportedStrategy = strategies.find { it.supports(event) }
            supportedStrategy?.let {
                @Suppress("UNCHECKED_CAST")
                (it as NotificationStrategy<NotificationPayload>).execute(event as Event<NotificationPayload>)
            }
        }
    }
}
