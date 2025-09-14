package com.soomsoom.backend.domain.activityhistory.model

import com.soomsoom.backend.domain.activity.model.enums.ActivityType
import java.time.LocalDateTime

class ActivityCompletionLog(
    val id: Long?,
    val userId: Long,
    val activityId: Long,
    val activityType: ActivityType,
    val createdAt: LocalDateTime?,
)
