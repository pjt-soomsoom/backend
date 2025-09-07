package com.soomsoom.backend.application.port.`in`.user.dto

import com.soomsoom.backend.domain.item.model.aggregate.Item
import com.soomsoom.backend.domain.item.model.enums.ItemType
import com.soomsoom.backend.domain.user.model.entity.OwnedItem

data class OwnedItemDto(
    val id: Long,
    val name: String,
    val itemType: ItemType,
    val imageUrl: String,
    val lottieUrl: String?,
)

fun OwnedItem.toDto(item: Item): OwnedItemDto {
    return OwnedItemDto(
        id = this.itemId,
        name = item.name,
        itemType = item.itemType,
        imageUrl = item.imageUrl,
        lottieUrl = item.lottieUrl
    )
}
