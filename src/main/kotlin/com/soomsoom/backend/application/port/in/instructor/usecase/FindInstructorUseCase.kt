package com.soomsoom.backend.application.port.`in`.instructor.usecase

import com.soomsoom.backend.application.port.`in`.instructor.command.FindInstructorCommand
import com.soomsoom.backend.domain.instructor.model.Instructor

interface FindInstructorUseCase {
    fun findById(command: FindInstructorCommand): Instructor?
}
