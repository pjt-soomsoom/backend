package com.soomsoom.backend.application.service.user.query

import com.soomsoom.backend.application.port.`in`.item.dto.toDto
import com.soomsoom.backend.application.port.`in`.user.dto.EquippedItemsDto
import com.soomsoom.backend.application.port.`in`.user.usecase.query.FindEquippedItemsUseCase
import com.soomsoom.backend.application.port.out.item.ItemPort
import com.soomsoom.backend.application.port.out.user.UserPort
import com.soomsoom.backend.common.exception.SoomSoomException
import com.soomsoom.backend.domain.user.UserErrorCode
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class FindEquippedItemsService(
    private val userPort: UserPort,
    private val itemPort: ItemPort,
) : FindEquippedItemsUseCase {
    override fun findEquippedItems(userId: Long): EquippedItemsDto {
        val user = userPort.findById(userId)
            ?: throw SoomSoomException(UserErrorCode.NOT_FOUND)

        val equippedItemIds = listOfNotNull(
            user.equippedItems.hat,
            user.equippedItems.eyewear,
            user.equippedItems.background,
            user.equippedItems.frame,
            user.equippedItems.floor,
            user.equippedItems.shelf
        )

        if (equippedItemIds.isEmpty()) {
            return EquippedItemsDto.empty() // 장착한 아이템이 없으면 빈 DTO 반환
        }

        val itemsById = itemPort.findAllByIds(equippedItemIds).associateBy { it.id }

        return EquippedItemsDto(
            hat = user.equippedItems.hat?.let { itemsById[it]?.toDto(user) },
            eyewear = user.equippedItems.eyewear?.let { itemsById[it]?.toDto(user) },
            background = user.equippedItems.background?.let { itemsById[it]?.toDto(user) },
            frame = user.equippedItems.frame?.let { itemsById[it]?.toDto(user) },
            floor = user.equippedItems.floor?.let { itemsById[it]?.toDto(user) },
            shelf = user.equippedItems.shelf?.let { itemsById[it]?.toDto(user) }
        )
    }
}
