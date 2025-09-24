package com.soomsoom.backend.application.port.`in`.mission.command

import com.soomsoom.backend.common.event.Payload

data class ProcessMissionProgressCommand(
    val payload: Payload,
)
