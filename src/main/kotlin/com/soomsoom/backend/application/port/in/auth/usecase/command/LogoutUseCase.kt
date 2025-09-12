package com.soomsoom.backend.application.port.`in`.auth.usecase.command

interface LogoutUseCase {
    fun logout(refreshToken: String)
}
