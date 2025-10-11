package com.soomsoom.backend.application.port.`in`.notification.usecase.command

interface DeleteUserDeviceUseCase {
    fun deleteByUserId(userId: Long)
}
