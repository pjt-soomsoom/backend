package com.soomsoom.backend.application.port.`in`.item.dto

import com.soomsoom.backend.application.port.`in`.upload.dto.FileUploadInfo

data class CreateItemResult(
    val itemId: Long,
    val imageUploadInfo: FileUploadInfo,
    val lottieUploadInfo: FileUploadInfo?,
)
