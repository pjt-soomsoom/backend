package com.soomsoom.backend.common.event.payload

import com.soomsoom.backend.common.event.NotificationPayload
import java.time.LocalDateTime

data class SchedulerTickNotificationPayload(
    val triggeredAt: LocalDateTime,
) : NotificationPayload
