package com.soomsoom.backend.common.event.payload

import com.soomsoom.backend.common.event.Payload

class ScreenTimeAccumulatedPayload(
    val userId: Long,
    val durationInSeconds: Int,
) : Payload
