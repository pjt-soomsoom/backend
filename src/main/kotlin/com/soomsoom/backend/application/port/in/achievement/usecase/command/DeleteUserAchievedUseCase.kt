package com.soomsoom.backend.application.port.`in`.achievement.usecase.command

interface DeleteUserAchievedUseCase {
    fun deleteByUserId(userId: Long)
}
