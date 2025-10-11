package com.soomsoom.backend.application.port.`in`.follow.usecase.command

interface DeleteFollowUseCase {
    fun deleteByUserId(userId: Long)
}
