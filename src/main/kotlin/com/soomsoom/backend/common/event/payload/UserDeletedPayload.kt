package com.soomsoom.backend.common.event.payload

import com.soomsoom.backend.common.event.Payload

data class UserDeletedPayload(
    val userId: Long,
) : Payload
