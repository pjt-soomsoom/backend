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
    val values: Set<Long>
        get() = listOfNotNull(hat, eyewear, background, frame, floor, shelf).toSet()

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
}
