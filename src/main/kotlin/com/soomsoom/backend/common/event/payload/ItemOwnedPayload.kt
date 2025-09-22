package com.soomsoom.backend.common.event.payload

import com.soomsoom.backend.common.event.Payload

class ItemOwnedPayload(
    val userId: Long,
    val itemId: Long,
) : Payload
