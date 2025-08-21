package com.soomsoom.backend.domain.activityhistory.model

import java.time.LocalDateTime

class UserActivitySummary(
    val id: Long?,
    val userId: Long,
    var totalPlaySeconds: Long,
    val createdAt: LocalDateTime?,
    val modifiedAt: LocalDateTime?,
) {
    fun addPlayTime(seconds: Int) {
        this.totalPlaySeconds += seconds
    }
}
