package com.soomsoom.backend.domain.user.model.vo

import com.soomsoom.backend.domain.item.model.enums.EquipSlot

data class EquippedItems(
    val hat: Long? = null,
    val eyewear: Long? = null,
    val background: Long? = null,
    val frame: Long? = null,
    val floor: Long? = null,
    val shelf: Long? = null,
) {
    fun equip(category: EquipSlot, itemId: Long): EquippedItems {
        return when (category) {
            EquipSlot.HAT -> this.copy(hat = itemId)
            EquipSlot.EYEWEAR -> this.copy(eyewear = itemId)
            EquipSlot.BACKGROUND -> this.copy(background = itemId)
            EquipSlot.FRAME -> this.copy(frame = itemId)
            EquipSlot.FLOOR -> this.copy(floor = itemId)
            EquipSlot.SHELF -> this.copy(shelf = itemId)
        }
    }

    fun unequip(category: EquipSlot): EquippedItems {
        return when (category) {
            EquipSlot.HAT -> this.copy(hat = null)
            EquipSlot.EYEWEAR -> this.copy(eyewear = null)
            EquipSlot.BACKGROUND -> this.copy(background = null)
            EquipSlot.FRAME -> this.copy(frame = null)
            EquipSlot.FLOOR -> this.copy(floor = null)
            EquipSlot.SHELF -> this.copy(shelf = null)
        }
    }
}
