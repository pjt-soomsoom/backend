package com.soomsoom.backend.application.port.`in`.instructor.usecase

import com.soomsoom.backend.adapter.`in`.web.api.instructor.request.InstructorSearchCriteria
import com.soomsoom.backend.application.port.`in`.instructor.dto.FindInstructorResult
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface SearchInstructorUseCase {
    fun search(criteria: InstructorSearchCriteria, pageable: Pageable): Page<FindInstructorResult>
}
