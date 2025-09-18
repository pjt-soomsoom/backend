package com.soomsoom.backend.application.port.`in`.notification.usecase.command.message

interface DeleteMessageVariationUseCase {
    fun command(messageId: Long)
}
