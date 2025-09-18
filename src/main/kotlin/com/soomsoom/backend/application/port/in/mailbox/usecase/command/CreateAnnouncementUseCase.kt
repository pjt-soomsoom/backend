package com.soomsoom.backend.application.port.`in`.mailbox.usecase.command

import com.soomsoom.backend.application.port.`in`.mailbox.command.CreateAnnouncementCommand

interface CreateAnnouncementUseCase {
    fun command(command: CreateAnnouncementCommand): Long
}
