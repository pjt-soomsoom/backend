package com.soomsoom.backend.common.event.payload

import com.soomsoom.backend.common.event.Payload
import com.soomsoom.backend.domain.item.model.enums.AcquisitionType

data class ItemPurchasedPayload(
    val userId: Long,
    val itemId: Long,
    val acquisitionType: AcquisitionType,
) : Payload
