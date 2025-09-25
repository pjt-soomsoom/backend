package com.soomsoom.backend.common.event.payload

import com.soomsoom.backend.common.event.Payload
import java.time.LocalDateTime

data class UserAuthenticatedPayload(
    val userId: Long,
    val authenticatedAt: LocalDateTime,
) : Payload
