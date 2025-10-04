package com.soomsoom.backend.application.service.useractivity.command

import com.soomsoom.backend.application.port.`in`.useractivity.command.RecordConnectionCommand
import com.soomsoom.backend.application.port.`in`.useractivity.usecase.command.RecordConnectionUseCase
import com.soomsoom.backend.application.port.out.useractivity.ConnectionLogPort
import com.soomsoom.backend.common.event.Event
import com.soomsoom.backend.common.event.EventType
import com.soomsoom.backend.common.event.payload.FirstConnectionPayload
import com.soomsoom.backend.common.utils.DateHelper
import com.soomsoom.backend.domain.useractivity.model.aggregate.ConnectionLog
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Service
@Transactional
class RecordConnectionService(
    private val connectionLogPort: ConnectionLogPort,
    private val dateHelper: DateHelper,
    private val eventPublisher: ApplicationEventPublisher,
) : RecordConnectionUseCase {
    override fun recordConnection(command: RecordConnectionCommand) {
        val now = LocalDateTime.now()
        val businessDay = dateHelper.getBusinessDay(now)
        val businessDate = dateHelper.getBusinessDate(dateHelper.toZonedDateTimeInUtc(now))

        val alreadyConnected = connectionLogPort.existsByUserIdAndCreatedAtBetween(
            userId = command.userId,
            from = businessDay.start,
            to = businessDay.end
        )

        if (alreadyConnected) {
            return
        }

        val newLog = ConnectionLog(userId = command.userId)
        connectionLogPort.save(newLog)

        val event = Event(
            eventType = EventType.FIRST_CONNECTION_OF_THE_DAY,
            payload = FirstConnectionPayload(
                userId = command.userId,
                businessDate = businessDate
            )
        )
        eventPublisher.publishEvent(event)
    }
}
