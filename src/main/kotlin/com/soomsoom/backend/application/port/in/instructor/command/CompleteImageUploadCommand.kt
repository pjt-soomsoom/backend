package com.soomsoom.backend.application.port.`in`.instructor.command

data class CompleteImageUploadCommand(
    val instructorId: Long,
    val fileKey: String,
)
