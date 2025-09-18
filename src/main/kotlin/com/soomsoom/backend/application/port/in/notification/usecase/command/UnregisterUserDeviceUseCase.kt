package com.soomsoom.backend.application.port.`in`.notification.usecase.command

import com.soomsoom.backend.application.port.`in`.notification.command.UnregisterUserDeviceCommand

interface UnregisterUserDeviceUseCase {
    fun command(command: UnregisterUserDeviceCommand)
}
