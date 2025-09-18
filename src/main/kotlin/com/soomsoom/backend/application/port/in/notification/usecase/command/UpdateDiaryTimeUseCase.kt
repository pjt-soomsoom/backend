package com.soomsoom.backend.application.port.`in`.notification.usecase.command

import com.soomsoom.backend.application.port.`in`.notification.command.UpdateDiaryTimeCommand

interface UpdateDiaryTimeUseCase {
    fun command(command: UpdateDiaryTimeCommand)
}
