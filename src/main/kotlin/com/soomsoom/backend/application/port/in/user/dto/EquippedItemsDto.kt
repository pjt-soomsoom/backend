package com.soomsoom.backend.application.port.`in`.user.dto

import com.soomsoom.backend.application.port.`in`.item.dto.ItemDto
import com.soomsoom.backend.application.port.`in`.item.dto.toDto
import com.soomsoom.backend.domain.item.model.aggregate.Item
import com.soomsoom.backend.domain.user.model.aggregate.User
import com.soomsoom.backend.domain.user.model.vo.EquippedItems

data class EquippedItemsDto(
    val hat: ItemDto?,
    val eyewear: ItemDto?,
    val background: ItemDto?,
    val frame: ItemDto?,
    val floor: ItemDto?,
    val shelf: ItemDto?,
) {
    companion object {
        /**
         * 장착한 아이템이 하나도 없는 상태를 나타내는 DTO를 생성
         */
        fun empty(): EquippedItemsDto {
            return EquippedItemsDto(
                hat = null,
                eyewear = null,
                background = null,
                frame = null,
                floor = null,
                shelf = null
            )
        }
    }
}

fun EquippedItems.toDto(user: User, equippedItemMap: Map<Long, Item>): EquippedItemsDto {
    fun getItemDtoForSlot(itemId: Long?): ItemDto? {
        return itemId?.let { id ->
            equippedItemMap[id]?.toDto(user)
        }
    }

    return EquippedItemsDto(
        hat = getItemDtoForSlot(this.hat),
        eyewear = getItemDtoForSlot(this.eyewear),
        background = getItemDtoForSlot(this.background),
        frame = getItemDtoForSlot(this.frame),
        floor = getItemDtoForSlot(this.floor),
        shelf = getItemDtoForSlot(this.shelf)
    )
}
