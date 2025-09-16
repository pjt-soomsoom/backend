package com.soomsoom.backend.domain.useractivity.model.aggregate

import java.time.LocalDateTime

data class ScreenTimeLog(
    val id: Long? = null,
    val userId: Long,
    val durationInSeconds: Int,
    val createdAt: LocalDateTime? = null,
)
