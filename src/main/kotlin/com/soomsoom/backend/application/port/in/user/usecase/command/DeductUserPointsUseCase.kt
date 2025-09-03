package com.soomsoom.backend.application.port.`in`.user.usecase.command

interface DeductUserPointsUseCase {
    fun deduct(command: DeductUserPointsCommand)
}
