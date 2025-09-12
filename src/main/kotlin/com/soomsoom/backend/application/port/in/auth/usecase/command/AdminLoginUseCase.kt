package com.soomsoom.backend.application.port.`in`.auth.usecase.command

import com.soomsoom.backend.application.port.`in`.auth.TokenInfo
import com.soomsoom.backend.application.port.`in`.auth.command.AdminLoginCommand

interface AdminLoginUseCase {
    fun adminLogin(command: AdminLoginCommand): TokenInfo
}
