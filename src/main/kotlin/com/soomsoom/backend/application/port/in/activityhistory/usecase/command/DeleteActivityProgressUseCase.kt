package com.soomsoom.backend.application.port.`in`.activityhistory.usecase.command

interface DeleteActivityProgressUseCase {
    fun deleteByUserId(userId: Long)
}
