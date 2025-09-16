package com.soomsoom.backend.application.port.`in`.useractivity.command

data class AccumulateScreenTimeCommand(
    val userId: Long,
    val durationInSeconds: Int,
)
