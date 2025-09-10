package com.soomsoom.backend.application.port.`in`.upload.dto

data class FileUploadInfo(
    val preSignedUrl: String,
    val fileKey: String,
)
