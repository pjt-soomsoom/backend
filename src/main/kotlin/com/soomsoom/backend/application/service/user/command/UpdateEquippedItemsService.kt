package com.soomsoom.backend.application.service.user.command

import com.soomsoom.backend.application.port.`in`.user.command.UpdateEquippedItemsCommand
import com.soomsoom.backend.application.port.`in`.user.dto.EquippedItemsDto
import com.soomsoom.backend.application.port.`in`.user.dto.toDto
import com.soomsoom.backend.application.port.`in`.user.usecase.command.UpdateEquippedItemsUseCase
import com.soomsoom.backend.application.port.out.item.ItemPort
import com.soomsoom.backend.application.port.out.user.UserPort
import com.soomsoom.backend.common.event.Event
import com.soomsoom.backend.common.event.EventType
import com.soomsoom.backend.common.event.payload.ItemsEquippedPayload
import com.soomsoom.backend.common.exception.SoomSoomException
import com.soomsoom.backend.domain.item.ItemErrorCode
import com.soomsoom.backend.domain.user.UserErrorCode
import org.springframework.context.ApplicationEventPublisher
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class UpdateEquippedItemsService(
    private val userPort: UserPort,
    private val itemPort: ItemPort,
    private val eventPublisher: ApplicationEventPublisher,
) : UpdateEquippedItemsUseCase {

    @PreAuthorize("#command.userId == authentication.principal.id")
    override fun updateEquippedItems(command: UpdateEquippedItemsCommand): EquippedItemsDto {
        val user = userPort.findByIdWithCollections(command.userId)
            ?: throw SoomSoomException(UserErrorCode.NOT_FOUND)

        // 아이템 착용 전에 실제 아이템을 가지고 있는지 검증
        command.itemsToEquip.values.forEach { itemIdToEquip ->
            if (!user.hasItem(itemIdToEquip)) {
                throw SoomSoomException(UserErrorCode.ITEM_NOT_OWNED)
            }
        }

        user.updateEquippedItems(command.itemsToEquip)

        val savedUser = userPort.save(user)

        val equippedItems = itemPort.findAllByIds(savedUser.getEquippedItemIds().toList())
        // 착용 요청한 아이템이 전부 존재하는 지 확인
        if (equippedItems.size != command.itemsToEquip.size) {
            throw SoomSoomException(ItemErrorCode.NOT_FOUND)
        }

        val equippedItemMap = equippedItems.associateBy { it.id }

        val payload = ItemsEquippedPayload(
            userId = savedUser.id!!,
            equippedItemIds = savedUser.getEquippedItemIds()
        )
        eventPublisher.publishEvent(Event(EventType.ITEMS_EQUIPPED, payload))
        return savedUser.equippedItems.toDto(savedUser, equippedItemMap)
    }
}
