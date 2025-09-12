package com.soomsoom.backend.application.port.`in`.auth.usecase.command

import com.soomsoom.backend.application.port.`in`.auth.TokenInfo
import com.soomsoom.backend.application.port.`in`.auth.command.DeviceAuthenticationCommand

interface AuthenticateWithDeviceUseCase {
    fun authenticate(command: DeviceAuthenticationCommand): TokenInfo
}
