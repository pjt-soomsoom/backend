package com.soomsoom.backend.application.port.`in`.item.command.item

import com.soomsoom.backend.domain.item.model.enums.AcquisitionType
import com.soomsoom.backend.domain.item.model.enums.EquipSlot
import com.soomsoom.backend.domain.item.model.enums.ItemType

data class UpdateItemInfoCommand(
    val itemId: Long,
    val name: String,
    val description: String?,
    val phrase: String?,
    val price: Int,
    val totalQuantity: Int?,
    val itemType: ItemType,
    val equipSlot: EquipSlot,
    val acquisitionType: AcquisitionType,
    val hasShadow: Boolean,
)
