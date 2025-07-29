package com.soomsoom.backend.application.port.`in`.instructor.usecase

import com.soomsoom.backend.application.port.`in`.instructor.command.CompleteImageUploadCommand
import com.soomsoom.backend.application.port.`in`.instructor.command.RegisterInstructorCommand
import com.soomsoom.backend.application.port.`in`.instructor.dto.RegisterInstructorResult

interface RegisterInstructorUseCase {
    fun register(command: RegisterInstructorCommand): RegisterInstructorResult
    fun completeImageUpload(command: CompleteImageUploadCommand)
}
