package com.soomsoom.backend.application.port.`in`.useractivity.usecase.command

interface DeleteScreenTimeLogUseCase {
    fun deleteByUserId(userId: Long)
}
