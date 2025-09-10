package com.soomsoom.backend.adapter.`in`.web.api.user.request

import com.soomsoom.backend.domain.item.model.enums.EquipSlot

data class UpdateEquippedItemsRequest(
    val itemsToEquip: Map<EquipSlot, Long>,
)
