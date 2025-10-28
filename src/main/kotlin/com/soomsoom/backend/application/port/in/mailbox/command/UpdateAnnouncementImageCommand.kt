package com.soomsoom.backend.application.port.`in`.mailbox.command

import com.soomsoom.backend.application.port.`in`.upload.command.ValidatedFileMetadata

data class UpdateAnnouncementImageCommand(
    val announcementId: Long,
    val imageMetadata: ValidatedFileMetadata,
)
