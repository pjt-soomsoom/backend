package com.soomsoom.backend.application.port.`in`.instructor.usecase.command

import com.soomsoom.backend.application.port.`in`.instructor.dto.RegisterInstructorResult
import com.soomsoom.backend.application.port.`in`.upload.command.ValidatedFileMetadata

interface UpdateInstructorProfileImageUrlUseCase {
    fun updateProfileImageUrl(instructorId: Long, fileMetadata: ValidatedFileMetadata): RegisterInstructorResult
}
