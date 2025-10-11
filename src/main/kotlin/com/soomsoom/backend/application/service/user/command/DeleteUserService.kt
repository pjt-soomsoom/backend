package com.soomsoom.backend.application.service.user.command

import com.soomsoom.backend.application.port.`in`.user.usecase.command.DeleteUserUseCase
import com.soomsoom.backend.application.port.out.user.UserPort
import com.soomsoom.backend.common.event.Event
import com.soomsoom.backend.common.event.EventType
import com.soomsoom.backend.common.event.payload.UserDeletedPayload
import com.soomsoom.backend.common.exception.SoomSoomException
import com.soomsoom.backend.domain.user.UserErrorCode
import org.springframework.context.ApplicationEventPublisher
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class DeleteUserService(
    private val eventPublisher: ApplicationEventPublisher,
    private val userPort: UserPort,
) : DeleteUserUseCase {

    @PreAuthorize("hasRole('ADMIN') or authentication.principal.id == #userId")
    @Transactional
    override fun delete(userId: Long) {
        userPort.findById(userId)
            ?: throw SoomSoomException(UserErrorCode.NOT_FOUND)

        val event = Event(
            eventType = EventType.USER_DELETED,
            payload = UserDeletedPayload(
                userId = userId
            )
        )

        eventPublisher.publishEvent(event)

        userPort.deleteByUserId(userId)
    }
}
