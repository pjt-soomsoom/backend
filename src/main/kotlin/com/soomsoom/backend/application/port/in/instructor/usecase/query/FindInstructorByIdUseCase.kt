package com.soomsoom.backend.application.port.`in`.instructor.usecase.query

import com.soomsoom.backend.application.port.`in`.instructor.dto.FindInstructorResult
import com.soomsoom.backend.domain.common.DeletionStatus

interface FindInstructorByIdUseCase {
    fun findById(instructorId: Long, deletionStatus: DeletionStatus): FindInstructorResult
}
