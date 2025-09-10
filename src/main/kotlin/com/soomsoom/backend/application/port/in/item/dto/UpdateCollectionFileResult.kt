package com.soomsoom.backend.application.port.`in`.item.dto

import com.soomsoom.backend.application.port.`in`.upload.dto.FileUploadInfo

data class UpdateCollectionFileResult(
    val collectionId: Long,
    val fileUploadInfo: FileUploadInfo?,
)
