package com.soomsoom.backend.application.port.`in`.notification.command

import com.soomsoom.backend.common.entity.enums.OSType

data class RegisterUserDeviceCommand(
    val userId: Long,
    val fcmToken: String,
    val osType: OSType,
)
