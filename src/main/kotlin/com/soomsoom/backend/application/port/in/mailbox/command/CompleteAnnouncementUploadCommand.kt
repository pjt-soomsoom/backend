package com.soomsoom.backend.application.port.`in`.mailbox.command

data class CompleteAnnouncementUploadCommand(
    val announcementId: Long,
    val imageFileKey: String,
)
