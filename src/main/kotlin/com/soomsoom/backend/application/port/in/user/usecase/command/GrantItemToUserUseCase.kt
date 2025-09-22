package com.soomsoom.backend.application.port.`in`.user.usecase.command

import com.soomsoom.backend.application.port.`in`.user.command.GrantItemToUserCommand
import com.soomsoom.backend.application.port.`in`.user.command.GrantItemsToUserCommand

interface GrantItemToUserUseCase {
    fun grantItemToUser(command: GrantItemToUserCommand)
    fun grantItemsToUser(command: GrantItemsToUserCommand)
    fun grantItemToAllUsers(itemId: Long): Int
}
