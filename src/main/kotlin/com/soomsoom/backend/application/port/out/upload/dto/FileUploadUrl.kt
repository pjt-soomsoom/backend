package com.soomsoom.backend.application.port.out.upload.dto

data class FileUploadUrl(
    val preSignedUrl: String,
    val fileKey: String,
)
