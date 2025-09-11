package com.soomsoom.backend.application.port.`in`.banner.dto

import com.soomsoom.backend.application.port.`in`.upload.dto.FileUploadInfo

data class CreateBannerResult(
    val bannerId: Long,
    val imageUploadInfo: FileUploadInfo,
)
