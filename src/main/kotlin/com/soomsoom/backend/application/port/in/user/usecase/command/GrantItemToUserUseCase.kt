package com.soomsoom.backend.application.port.`in`.user.usecase.command

import com.soomsoom.backend.application.port.`in`.user.command.GrantItemToUserCommand

interface GrantItemToUserUseCase {
    fun grantItemToUser(command: GrantItemToUserCommand)
}
