package com.soomsoom.backend.application.port.`in`.item.command.item

import com.soomsoom.backend.application.port.`in`.upload.command.ValidatedFileMetadata
import com.soomsoom.backend.domain.item.model.enums.AcquisitionType
import com.soomsoom.backend.domain.item.model.enums.EquipSlot
import com.soomsoom.backend.domain.item.model.enums.ItemType

data class CreateItemCommand(
    val name: String,
    val description: String?,
    val phrase: String?,
    val itemType: ItemType,
    val equipSlot: EquipSlot,
    val acquisitionType: AcquisitionType,
    val price: Int,
    val hasShadow: Boolean,
    val imageMetadata: ValidatedFileMetadata,
    val lottieMetadata: ValidatedFileMetadata?,
    val totalQuantity: Int?, // null이면 무제한
)
