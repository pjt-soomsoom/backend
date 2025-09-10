package com.soomsoom.backend.common.event.payload

import com.soomsoom.backend.common.event.Payload

data class ItemsEquippedPayload(
    var userId: Long,
    var equippedItemIds: Set<Long>,
) : Payload
