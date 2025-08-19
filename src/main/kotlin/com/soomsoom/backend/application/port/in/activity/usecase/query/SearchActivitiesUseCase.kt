package com.soomsoom.backend.application.port.`in`.activity.usecase.query

import com.soomsoom.backend.application.port.`in`.activity.dto.ActivityResult
import com.soomsoom.backend.application.port.`in`.activity.query.SearchActivitiesCriteria
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface SearchActivitiesUseCase {
    fun search(criteria: SearchActivitiesCriteria, pageable: Pageable): Page<ActivityResult>
}
