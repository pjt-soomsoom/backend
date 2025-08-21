package com.soomsoom.backend.domain.activityhistory.model

import java.time.LocalDateTime

class ActivityCompletionLog(
    val id: Long?,
    val userId: Long,
    val activityId: Long,
    val createdAt: LocalDateTime?,
)
