package com.soomsoom.backend.common.event.payload

import com.soomsoom.backend.common.event.Payload

data class UserAuthenticatedPayload(
    val userId: Long,
) : Payload
