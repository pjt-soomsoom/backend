package com.soomsoom.backend.application.port.`in`.activityhistory.usecase.command

interface DeleteActivityCompletionLogUseCase {
    fun deleteByUserId(userId: Long)
}
