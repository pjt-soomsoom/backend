package com.soomsoom.backend.application.port.`in`.instructor.usecase.query

import com.soomsoom.backend.application.port.`in`.instructor.dto.FindInstructorResult
import com.soomsoom.backend.application.port.`in`.instructor.query.SearchInstructorsCriteria
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface SearchInstructorUseCase {
    fun search(criteria: SearchInstructorsCriteria, pageable: Pageable): Page<FindInstructorResult>
}
