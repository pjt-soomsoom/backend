package com.soomsoom.backend.application.port.`in`.activityhistory.dto

data class ActivityCompleteResult(
    val activityId: Long,
    val completionEffectTexts: List<String>,
    val rewardable: Boolean,
)
