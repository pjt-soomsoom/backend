package com.soomsoom.backend.application.port.`in`.activity.usecase.query

import com.soomsoom.backend.application.port.`in`.activity.dto.InstructorActivitySummaryResult
import com.soomsoom.backend.application.port.`in`.activity.query.SearchInstructorActivitiesCriteria
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface SearchInstructorActivitiesUseCase {
    fun search(criteria: SearchInstructorActivitiesCriteria, pageable: Pageable): Page<InstructorActivitySummaryResult>
}
