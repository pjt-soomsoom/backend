// src/main/kotlin/com/soomsoom/backend/application/service/achievement/command/strategy/ActivityCompletedProgressUpdateStrategy.kt
package com.soomsoom.backend.application.service.achievement.command.strategy

import com.soomsoom.backend.common.event.Event
import com.soomsoom.backend.common.event.Payload
import com.soomsoom.backend.common.event.payload.ActivityCompletedPayload
import com.soomsoom.backend.domain.activity.model.ActivityType
import org.springframework.stereotype.Component

@Component
class ActivityCompletedProgressUpdateStrategy(
    strategies: List<ActivityTypeProgressUpdateStrategy>,
) : ProgressUpdateStrategy {

    private val strategyMap: Map<ActivityType, ActivityTypeProgressUpdateStrategy> =
        strategies.associateBy { it.supports() }

    override fun supports(event: Event<out Payload>) = event.payload is ActivityCompletedPayload

    override fun update(event: Event<out Payload>) {
        val payload = event.payload as ActivityCompletedPayload
        strategyMap[payload.activityType]?.update(payload)
    }
}
