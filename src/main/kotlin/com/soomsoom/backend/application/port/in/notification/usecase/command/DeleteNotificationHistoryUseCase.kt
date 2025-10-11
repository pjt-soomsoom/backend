package com.soomsoom.backend.application.port.`in`.notification.usecase.command

interface DeleteNotificationHistoryUseCase {
    fun deleteByUserId(userId: Long)
}
