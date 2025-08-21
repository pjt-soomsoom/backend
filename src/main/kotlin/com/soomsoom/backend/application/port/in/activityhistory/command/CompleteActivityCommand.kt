package com.soomsoom.backend.application.port.`in`.activityhistory.command

data class CompleteActivityCommand(
    val userId: Long,
    val activityId: Long,
)
