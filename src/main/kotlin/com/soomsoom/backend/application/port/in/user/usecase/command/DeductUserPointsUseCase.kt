package com.soomsoom.backend.application.port.`in`.user.usecase.command

import com.soomsoom.backend.application.port.`in`.user.command.DeductUserPointsCommand

interface DeductUserPointsUseCase {
    fun deduct(command: DeductUserPointsCommand)
}
