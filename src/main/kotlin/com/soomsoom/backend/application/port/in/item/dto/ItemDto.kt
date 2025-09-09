package com.soomsoom.backend.application.port.`in`.item.dto

import com.soomsoom.backend.domain.item.model.aggregate.Item
import com.soomsoom.backend.domain.item.model.enums.AcquisitionType
import com.soomsoom.backend.domain.item.model.enums.EquipSlot
import com.soomsoom.backend.domain.item.model.enums.ItemType
import com.soomsoom.backend.domain.user.model.aggregate.User
import java.time.LocalDateTime

data class ItemDto(
    val id: Long,
    val name: String,
    val description: String?,
    val phrase: String?,
    val itemType: ItemType,
    val equipSlot: EquipSlot,
    val acquisitionType: AcquisitionType,
    val price: Int,
    val imageUrl: String,
    val lottieUrl: String?,
    val isSoldOut: Boolean,
    val isOwned: Boolean,
    val isEquipped: Boolean,
    val createdAt: LocalDateTime,
    val modifiedAt: LocalDateTime,
    val deletedAt: LocalDateTime?,
)

fun Item.toDto(user: User): ItemDto {
    val isOwned = user.hasItem(this.id)
    val isEquipped = user.equippedItems.let { equipped ->
        when (this.equipSlot) {
            EquipSlot.HAT -> equipped.hat == this.id
            EquipSlot.EYEWEAR -> equipped.eyewear == this.id
            EquipSlot.BACKGROUND -> equipped.background == this.id
            EquipSlot.FRAME -> equipped.frame == this.id
            EquipSlot.FLOOR -> equipped.floor == this.id
            EquipSlot.SHELF -> equipped.shelf == this.id
        }
    }

    return ItemDto(
        id = this.id,
        name = this.name,
        description = this.description,
        phrase = this.phrase,
        itemType = this.itemType,
        equipSlot = this.equipSlot,
        acquisitionType = this.acquisitionType,
        price = this.price.value,
        imageUrl = this.imageUrl,
        lottieUrl = this.lottieUrl,
        isSoldOut = this.stock.isSoldOut(),
        isOwned = isOwned,
        isEquipped = isEquipped,
        createdAt = this.createdAt!!,
        modifiedAt = this.modifiedAt!!,
        deletedAt = this.deletedAt
    )
}

fun Item.toAdminDto(): ItemDto {
    return ItemDto(
        id = this.id,
        name = this.name,
        description = this.description,
        phrase = this.phrase,
        itemType = this.itemType,
        equipSlot = this.equipSlot,
        acquisitionType = this.acquisitionType,
        price = this.price.value,
        imageUrl = this.imageUrl,
        lottieUrl = this.lottieUrl,
        isSoldOut = this.stock.isSoldOut(),
        isOwned = false,
        isEquipped = false,
        createdAt = this.createdAt!!,
        modifiedAt = this.modifiedAt!!,
        deletedAt = this.deletedAt
    )
}
