package com.soomsoom.backend.application.service.achievement.command.strategy

import com.soomsoom.backend.common.event.Event
import com.soomsoom.backend.common.event.Payload

interface ProgressUpdateStrategy {
    fun supports(event: Event<out Payload>): Boolean
    fun update(event: Event<out Payload>)
}
