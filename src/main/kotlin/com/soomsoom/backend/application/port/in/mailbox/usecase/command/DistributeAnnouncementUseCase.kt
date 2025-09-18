package com.soomsoom.backend.application.port.`in`.mailbox.usecase.command

interface DistributeAnnouncementUseCase {
    fun command(announcementId: Long)
}
