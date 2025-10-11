package com.soomsoom.backend.application.port.`in`.useractivity.usecase.command

interface DeleteConnectionLogUseCase {
    fun deleteByUserId(userId: Long)
}
