package com.soomsoom.backend.application.port.`in`.auth.usecase.command

import com.soomsoom.backend.application.port.`in`.auth.TokenInfo
import com.soomsoom.backend.application.port.`in`.auth.command.AdminSignUpCommand

interface AdminSignUpUseCase {

    fun adminSignUp(command: AdminSignUpCommand): TokenInfo
}
