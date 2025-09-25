package com.soomsoom.backend.common.event.payload

import com.soomsoom.backend.common.event.Payload

data class PageVisitedPayload(
    val userId: Long,
    val pageIdentifier: String,
) : Payload
