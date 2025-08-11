package com.soomsoom.backend.application.port.`in`.instructor.command

data class UpdateInstructorInfoCommand(
    val instructorId: Long,
    val name: String,
    val bio: String?,
)
