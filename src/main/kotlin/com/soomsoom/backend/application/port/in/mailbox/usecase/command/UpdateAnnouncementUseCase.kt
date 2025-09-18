package com.soomsoom.backend.application.port.`in`.mailbox.usecase.command

import com.soomsoom.backend.application.port.`in`.mailbox.command.UpdateAnnouncementCommand

interface UpdateAnnouncementUseCase {
    fun command(command: UpdateAnnouncementCommand)
}
