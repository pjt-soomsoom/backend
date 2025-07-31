package com.soomsoom.backend.application.port.`in`.instructor.command

import com.soomsoom.backend.application.port.`in`.upload.command.ValidatedFileMetadata

data class RegisterInstructorCommand(
    val name: String,
    val profileImageMetadata: ValidatedFileMetadata?,
    val bio: String?,
)
