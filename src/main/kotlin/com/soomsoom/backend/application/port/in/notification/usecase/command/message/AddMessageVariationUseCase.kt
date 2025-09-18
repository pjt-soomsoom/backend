package com.soomsoom.backend.application.port.`in`.notification.usecase.command.message

import com.soomsoom.backend.application.port.`in`.notification.command.message.AddMessageVariationCommand

interface AddMessageVariationUseCase {
    fun command(command: AddMessageVariationCommand): Long
}
