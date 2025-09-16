package com.soomsoom.backend.common.event.payload

import com.soomsoom.backend.common.event.Payload
import java.time.LocalDate

class FirstConnectionPayload(
    val userId: Long,
    val businessDate: LocalDate,
) : Payload
