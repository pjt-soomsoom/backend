package com.soomsoom.backend.application.port.`in`.auth.usecase

import com.soomsoom.backend.application.port.`in`.auth.TokenInfo
import com.soomsoom.backend.application.port.`in`.auth.command.AdminLoginCommand

interface AdminLoginUseCase {
    fun adminLogin(command: AdminLoginCommand): TokenInfo
}
