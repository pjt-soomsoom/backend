package com.soomsoom.backend.application.port.`in`.instructor.command

data class RegisterInstructorCommand(
    val name: String,
    val profileImageUrl: String?,
    val bio: String?,
)
