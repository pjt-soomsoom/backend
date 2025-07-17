package com.soomsoom.backend.application.service.instructor

import com.soomsoom.backend.application.port.`in`.instructor.command.FindInstructorCommand
import com.soomsoom.backend.application.port.`in`.instructor.usecase.FindInstructorUseCase
import com.soomsoom.backend.application.port.out.instructor.InstructorPort
import com.soomsoom.backend.domain.instructor.model.Instructor
import org.springframework.data.crossstore.ChangeSetPersister.NotFoundException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class FindInstructorService(
    private val instructorPort: InstructorPort,
) : FindInstructorUseCase {
    override fun findById(command: FindInstructorCommand): Instructor? {
        return instructorPort.findById(command.id) ?: throw (NotFoundException())
    }
}
