package com.soomsoom.backend.application.port.`in`.item.dto

import com.soomsoom.backend.application.port.`in`.upload.dto.FileUploadInfo

data class CreateCollectionResult(
    val collectionId: Long,
    val imageUploadInfo: FileUploadInfo,
    val lottieUploadInfo: FileUploadInfo?,
)
