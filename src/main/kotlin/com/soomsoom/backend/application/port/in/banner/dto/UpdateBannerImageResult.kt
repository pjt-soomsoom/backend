package com.soomsoom.backend.application.port.`in`.banner.dto

import com.soomsoom.backend.application.port.`in`.upload.dto.FileUploadInfo

data class UpdateBannerImageResult(
    val bannerId: Long,
    val imageUploadInfo: FileUploadInfo,
)
