package com.soomsoom.backend.application.port.`in`.mailbox.command

data class CompleteAnnouncementImageUpdateCommand(
    val announcementId: Long,
    val imageFileKey: String,
)
