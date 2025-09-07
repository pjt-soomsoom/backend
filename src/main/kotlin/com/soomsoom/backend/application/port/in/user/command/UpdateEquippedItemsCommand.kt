package com.soomsoom.backend.application.port.`in`.user.command

import com.soomsoom.backend.domain.item.model.enums.EquipSlot

data class UpdateEquippedItemsCommand(
    val userId: Long,
    val itemsToEquip: Map<EquipSlot, Long>,
)
