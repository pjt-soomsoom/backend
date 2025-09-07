package com.soomsoom.backend.application.service.user.command

import com.soomsoom.backend.application.port.`in`.user.command.UpdateEquippedItemsCommand
import com.soomsoom.backend.application.port.`in`.user.dto.EquippedItemsDto
import com.soomsoom.backend.application.port.`in`.user.dto.toDto
import com.soomsoom.backend.application.port.`in`.user.usecase.command.UpdateEquippedItemsUseCase
import com.soomsoom.backend.application.port.out.item.ItemPort
import com.soomsoom.backend.application.port.out.user.UserPort
import com.soomsoom.backend.common.exception.SoomSoomException
import com.soomsoom.backend.domain.user.UserErrorCode
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class UpdateEquippedItemsService(
    private val userPort: UserPort,
    private val itemPort: ItemPort,
) : UpdateEquippedItemsUseCase {

    @PreAuthorize("#command.userId == authentication.principal.id")
    override fun updateEquippedItems(command: UpdateEquippedItemsCommand): EquippedItemsDto {
        val user = userPort.findById(command.userId)
            ?: throw SoomSoomException(UserErrorCode.NOT_FOUND)

        command.itemsToEquip.values.forEach { itemIdToEquip ->
            check(user.hasItem(itemIdToEquip)) {
                throw SoomSoomException(UserErrorCode.ITEM_NOT_OWNED)
            }
        }

        user.updateEquippedItems(command.itemsToEquip)

        val savedUser = userPort.save(user)

        val equippedItemIds = savedUser.equippedItems.let {
            listOfNotNull(it.hat, it.eyewear, it.background, it.floor, it.frame, it.shelf)
        }
        val equippedItems = itemPort.findAllByIds(equippedItemIds)
        val equippedItemMap = equippedItems.associateBy { it.id }

        return savedUser.equippedItems.toDto(savedUser, equippedItemMap)
    }
}
