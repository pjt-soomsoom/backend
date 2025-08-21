package com.soomsoom.backend.application.port.`in`.activityhistory.dto

data class FindActivityProgressResult(
    val activityId: Long,
    val progressSeconds: Int,
)
