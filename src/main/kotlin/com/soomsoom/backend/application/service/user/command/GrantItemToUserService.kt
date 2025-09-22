package com.soomsoom.backend.application.service.user.command

import com.soomsoom.backend.application.port.`in`.user.command.GrantItemToUserCommand
import com.soomsoom.backend.application.port.`in`.user.command.GrantItemsToUserCommand
import com.soomsoom.backend.application.port.`in`.user.usecase.command.GrantItemToUserUseCase
import com.soomsoom.backend.application.port.out.user.UserPort
import com.soomsoom.backend.common.event.Event
import com.soomsoom.backend.common.event.EventType
import com.soomsoom.backend.common.event.payload.ItemOwnedPayload
import com.soomsoom.backend.common.exception.SoomSoomException
import com.soomsoom.backend.domain.user.UserErrorCode
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class GrantItemToUserService(
    private val userPort: UserPort,
    private val eventPublisher: ApplicationEventPublisher,
) : GrantItemToUserUseCase {
    override fun grantItemToUser(command: GrantItemToUserCommand) {
        val user = userPort.findByIdWithCollections(command.userId)
            ?: throw SoomSoomException(UserErrorCode.NOT_FOUND)

        if (!user.hasItem(command.itemId)) {
            user.ownItem(command.itemId)
            userPort.save(user)
        }

        val payload = ItemOwnedPayload(
            userId = command.userId,
            itemId = command.itemId
        )

        eventPublisher.publishEvent(
            Event(
                eventType = EventType.ITEM_OWNED,
                payload = payload
            )
        )
    }

    override fun grantItemsToUser(command: GrantItemsToUserCommand) {
        val user = (
            userPort.findByIdWithCollections(command.userId)
                ?: throw SoomSoomException(UserErrorCode.NOT_FOUND)
            )

        val ownItems = user.ownItems(command.itemIds)
        userPort.save(user)

        ownItems.forEach { itemId ->
            eventPublisher.publishEvent(
                Event(
                    eventType = EventType.ITEM_OWNED,
                    payload = ItemOwnedPayload(
                        userId = command.userId,
                        itemId = itemId
                    )
                )
            )
        }
    }

    override fun grantItemToAllUsers(itemId: Long): Int {
        return userPort.grantItemToAllUsers(itemId)
    }
}
