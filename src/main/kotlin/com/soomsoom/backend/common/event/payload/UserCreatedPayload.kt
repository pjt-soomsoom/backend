package com.soomsoom.backend.common.event.payload

import com.soomsoom.backend.common.event.Payload

class UserCreatedPayload(
    var userId: Long,
) : Payload
