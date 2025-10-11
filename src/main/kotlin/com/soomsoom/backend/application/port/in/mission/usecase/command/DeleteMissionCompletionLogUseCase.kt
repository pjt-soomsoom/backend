package com.soomsoom.backend.application.port.`in`.mission.usecase.command

interface DeleteMissionCompletionLogUseCase {
    fun deleteByUserId(userId: Long)
}
