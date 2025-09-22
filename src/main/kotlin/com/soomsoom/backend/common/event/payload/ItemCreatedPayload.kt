package com.soomsoom.backend.common.event.payload

import com.soomsoom.backend.common.event.Payload
import com.soomsoom.backend.domain.item.model.enums.AcquisitionType

class ItemCreatedPayload(
    val itemId: Long,
    val acquisitionType: AcquisitionType,
) : Payload
