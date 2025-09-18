package com.soomsoom.backend.application.port.`in`.notification.command

data class UnregisterUserDeviceCommand(
    val fcmToken: String,
)
