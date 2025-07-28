package com.soomsoom.backend.application.port.`in`.instructor.dto

data class RegisterInstructorResult(
    val instructorId: Long,
    val preSignedUrl: String,
    val fileKey: String,
)
