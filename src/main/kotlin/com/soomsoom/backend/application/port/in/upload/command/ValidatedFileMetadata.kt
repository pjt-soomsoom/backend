package com.soomsoom.backend.application.port.`in`.upload.command

data class ValidatedFileMetadata(
    val filename: String,
    val contentType: String,
)
