package com.soomsoom.backend.application.port.`in`.user.usecase.command

interface DeleteCartUseCase {
    fun deleteByUserId(userId: Long)
}
