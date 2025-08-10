package com.soomsoom.backend.application.port.`in`.instructor.command

import com.soomsoom.backend.application.port.`in`.upload.command.ValidatedFileMetadata

data class UpdateProfileImageUrlCommand(
    val instructorId: Long,
    val profileImageMetadata: ValidatedFileMetadata?,
)
