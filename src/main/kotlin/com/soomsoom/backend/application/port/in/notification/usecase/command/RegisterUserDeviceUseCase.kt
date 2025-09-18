package com.soomsoom.backend.application.port.`in`.notification.usecase.command

import com.soomsoom.backend.application.port.`in`.notification.command.RegisterUserDeviceCommand

interface RegisterUserDeviceUseCase {
    fun command(command: RegisterUserDeviceCommand)
}
