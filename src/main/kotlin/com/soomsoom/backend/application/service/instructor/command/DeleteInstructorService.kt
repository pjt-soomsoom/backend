package com.soomsoom.backend.application.service.instructor.command

import com.soomsoom.backend.application.port.`in`.instructor.usecase.command.DeleteInstructorUseCase
import com.soomsoom.backend.application.port.out.instructor.InstructorPort
import com.soomsoom.backend.common.exception.SoomSoomException
import com.soomsoom.backend.domain.instructor.InstructorErrorCode
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class DeleteInstructorService(
    private val instructorPort: InstructorPort,
) : DeleteInstructorUseCase {

    @PreAuthorize("hasRole('ADMIN')")
    override fun delete(instructorId: Long) {
        instructorPort.findById(instructorId)
            ?. apply { delete() }
            ?. let(instructorPort::save)
            ?: throw SoomSoomException(InstructorErrorCode.NOT_FOUND)
    }
}
