package com.soomsoom.backend.application.service.achievement.command.strategy

import com.soomsoom.backend.common.event.payload.ActivityCompletedPayload
import com.soomsoom.backend.domain.activity.model.ActivityType

interface ActivityTypeProgressUpdateStrategy {
    fun supports(): ActivityType
    fun update(payload: ActivityCompletedPayload)
}
