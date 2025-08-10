package com.soomsoom.backend.application.port.`in`.instructor.usecase

import com.soomsoom.backend.adapter.`in`.web.api.common.DeletionStatus
import com.soomsoom.backend.application.port.`in`.instructor.dto.FindInstructorResult

interface FindInstructorByIdUseCase {
    fun findById(instructorId: Long, deletionStatus: DeletionStatus): FindInstructorResult
}
