package com.soomsoom.backend.application.port.`in`.mailbox.usecase.command

interface DeleteUserAnnouncementsUseCase {
    fun command(announcementId: Long)
}
