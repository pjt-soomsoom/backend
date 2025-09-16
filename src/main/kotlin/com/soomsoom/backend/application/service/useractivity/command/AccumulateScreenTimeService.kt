package com.soomsoom.backend.application.service.useractivity.command

import com.soomsoom.backend.application.port.`in`.useractivity.command.AccumulateScreenTimeCommand
import com.soomsoom.backend.application.port.`in`.useractivity.usecase.command.AccumulateScreenTimeUseCase
import com.soomsoom.backend.application.port.out.useractivity.ScreenTimeLogPort
import com.soomsoom.backend.common.event.Event
import com.soomsoom.backend.common.event.EventType
import com.soomsoom.backend.common.event.payload.ScreenTimeAccumulatedPayload
import com.soomsoom.backend.domain.useractivity.model.aggregate.ScreenTimeLog
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class AccumulateScreenTimeService(
    private val screenTimeLogPort: ScreenTimeLogPort,
    private val eventPublisher: ApplicationEventPublisher,
) : AccumulateScreenTimeUseCase {
    override fun accumulate(command: AccumulateScreenTimeCommand) {
        val newLog = ScreenTimeLog(
            userId = command.userId,
            durationInSeconds = command.durationInSeconds
        )

        screenTimeLogPort.save(newLog)

        val event = Event(
            eventType = EventType.SCREEN_TIME_ACCUMULATED,
            payload = ScreenTimeAccumulatedPayload(
                userId = command.userId,
                durationInSeconds = command.durationInSeconds
            )
        )
        eventPublisher.publishEvent(event)
    }
}
