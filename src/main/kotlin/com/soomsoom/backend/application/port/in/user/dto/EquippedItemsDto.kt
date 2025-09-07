package com.soomsoom.backend.application.port.`in`.user.dto

import com.soomsoom.backend.domain.item.model.aggregate.Item
import com.soomsoom.backend.domain.user.model.entity.OwnedItem
import com.soomsoom.backend.domain.user.model.vo.EquippedItems

data class EquippedItemsDto(
    val hat: OwnedItemDto?,
    val eyewear: OwnedItemDto?,
    val background: OwnedItemDto?,
    val frame: OwnedItemDto?,
    val floor: OwnedItemDto?,
    val shelf: OwnedItemDto?,
)

fun EquippedItems.toDto(items: Map<Long, Item>): EquippedItemsDto {
    val toOwnedItemDto: (Long?) -> OwnedItemDto? = { itemId ->
        itemId?.let { id ->
            items[id]?.let { item ->
                OwnedItem(userId = 0L, itemId = id, acquisitionType = item.acquisitionType).toDto(item)
            }
        }
    }

    return EquippedItemsDto(
        hat = toOwnedItemDto(this.hat),
        eyewear = toOwnedItemDto(this.eyewear),
        background = toOwnedItemDto(this.background),
        frame = toOwnedItemDto(this.frame),
        floor = toOwnedItemDto(this.floor),
        shelf = toOwnedItemDto(this.shelf)
    )
}
