package com.soomsoom.backend.application.port.`in`.mailbox.dto

import com.soomsoom.backend.application.port.`in`.upload.dto.FileUploadInfo

data class UpdateAnnouncementFileResult(
    val announcementId: Long,
    val fileUploadInfo: FileUploadInfo?,
)
