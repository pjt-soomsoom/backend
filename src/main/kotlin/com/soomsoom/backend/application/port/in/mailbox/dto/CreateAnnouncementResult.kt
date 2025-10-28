package com.soomsoom.backend.application.port.`in`.mailbox.dto

import com.soomsoom.backend.application.port.`in`.upload.dto.FileUploadInfo

data class CreateAnnouncementResult(
    val announcementId: Long,
    val imageUploadInfo: FileUploadInfo?,
)
