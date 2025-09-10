package com.soomsoom.backend.application.port.`in`.item.dto

import com.soomsoom.backend.application.port.`in`.upload.dto.FileUploadInfo

data class UpdateItemFileResult(
    val itemId: Long,
    val fileUploadInfo: FileUploadInfo?,
)
