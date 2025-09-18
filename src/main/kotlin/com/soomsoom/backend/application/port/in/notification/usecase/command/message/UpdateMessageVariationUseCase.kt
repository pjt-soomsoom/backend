package com.soomsoom.backend.application.port.`in`.notification.usecase.command.message

import com.soomsoom.backend.application.port.`in`.notification.command.message.UpdateMessageVariationCommand

interface UpdateMessageVariationUseCase {
    fun command(command: UpdateMessageVariationCommand)
}
