package com.soomsoom.backend.application.port.`in`.instructor.usecase

import com.soomsoom.backend.application.port.`in`.instructor.command.UpdateInstructorInfoCommand
import com.soomsoom.backend.application.port.`in`.instructor.dto.FindInstructorResult

interface UpdateInstructorInfoUseCase {
    fun updateInfo(command: UpdateInstructorInfoCommand): FindInstructorResult
}
