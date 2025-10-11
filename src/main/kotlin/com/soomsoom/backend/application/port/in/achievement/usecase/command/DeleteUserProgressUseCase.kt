package com.soomsoom.backend.application.port.`in`.achievement.usecase.command

interface DeleteUserProgressUseCase {
    fun deleteByUserId(userId: Long)
}
