package com.soomsoom.backend.application.service.instructor

import com.soomsoom.backend.application.port.`in`.instructor.command.RegisterInstructorCommand
import com.soomsoom.backend.application.port.`in`.instructor.usecase.RegisterInstructorUseCase
import com.soomsoom.backend.application.port.out.instructor.InstructorPort
import com.soomsoom.backend.domain.instructor.model.Instructor
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class RegisterInstructorService(
    private val instructorPort: InstructorPort
): RegisterInstructorUseCase{

    override fun register(command: RegisterInstructorCommand): Instructor {
        return Instructor(null, command.name, command.profileImageUrl, command.bio)
            .let(instructorPort::save)
    }
}
