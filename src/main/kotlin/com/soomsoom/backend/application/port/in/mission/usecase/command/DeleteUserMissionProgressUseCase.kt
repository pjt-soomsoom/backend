package com.soomsoom.backend.application.port.`in`.mission.usecase.command

interface DeleteUserMissionProgressUseCase {
    fun deleteByUserId(userId: Long)
}
