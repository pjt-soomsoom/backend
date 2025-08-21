package com.soomsoom.backend.application.port.`in`.activityhistory.dto

data class FindMySummaryResult(
    val diaryCount: Long,
    val activityCount: Long,
    val totalActivitySeconds: Long,
)
