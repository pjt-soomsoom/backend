package com.soomsoom.backend.application.port.`in`.notification.usecase.command

interface DeleteUserNotificationSettingUseCase {
    fun deleteByUserId(userId: Long)
}
