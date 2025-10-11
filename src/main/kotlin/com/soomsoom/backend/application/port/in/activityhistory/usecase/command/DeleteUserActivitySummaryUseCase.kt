package com.soomsoom.backend.application.port.`in`.activityhistory.usecase.command

interface DeleteUserActivitySummaryUseCase {
    fun deleteByUserId(userId: Long)
}
