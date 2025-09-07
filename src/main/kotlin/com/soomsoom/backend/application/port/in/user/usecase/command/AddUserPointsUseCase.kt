package com.soomsoom.backend.application.port.`in`.user.usecase.command

import com.soomsoom.backend.application.port.`in`.user.command.AddUserPointsCommand

interface AddUserPointsUseCase {
    fun addUserPoints(command: AddUserPointsCommand): Int
}
