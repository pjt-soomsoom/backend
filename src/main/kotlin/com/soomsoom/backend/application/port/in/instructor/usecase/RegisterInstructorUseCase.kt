package com.soomsoom.backend.application.port.`in`.instructor.usecase

import com.soomsoom.backend.application.port.`in`.instructor.command.RegisterInstructorCommand
import com.soomsoom.backend.domain.instructor.model.Instructor

interface RegisterInstructorUseCase {
    fun register(command: RegisterInstructorCommand): Instructor
}
