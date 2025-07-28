package com.soomsoom.backend.application.port.`in`.instructor.command

import com.soomsoom.backend.application.port.`in`.common.FileMetadata

data class RegisterInstructorCommand(
    val name: String,
    val profileImageMetadata: FileMetadata?,
    val bio: String?,
)
