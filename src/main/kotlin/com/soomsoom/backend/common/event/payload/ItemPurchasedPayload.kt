package com.soomsoom.backend.common.event.payload

import com.soomsoom.backend.common.event.Payload
import com.soomsoom.backend.domain.item.model.enums.AcquisitionType
import java.time.LocalDateTime

data class ItemPurchasedPayload(
    val userId: Long,
    val itemId: Long,
    val acquisitionType: AcquisitionType,
    val occurredOn: LocalDateTime = LocalDateTime.now()
) : Payload
