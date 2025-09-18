package com.soomsoom.backend.adapter.`in`.scheduler

import com.soomsoom.backend.common.event.Event
import com.soomsoom.backend.common.event.EventType
import com.soomsoom.backend.common.event.payload.SchedulerTickNotificationPayload
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock
import org.springframework.context.ApplicationEventPublisher
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import java.time.LocalDateTime

@Component
class NotificationScheduler(
    private val eventPublisher: ApplicationEventPublisher,
) {
    /**
     * 매 분 0초에 실행됩니다.
     */
    @Scheduled(cron = "0 * * * * *")
    @SchedulerLock(name = "notificationScheduler_everyMinute", lockAtLeastFor = "PT50S", lockAtMostFor = "PT55S")
    fun runEveryMinuteTasks() {
        val payload = SchedulerTickNotificationPayload(triggeredAt = LocalDateTime.now())
        val event = Event(EventType.SCHEDULER_TICK, payload)
        eventPublisher.publishEvent(event)
    }
}
