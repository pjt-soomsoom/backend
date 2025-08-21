package com.soomsoom.backend.application.service.instructor.query

import com.soomsoom.backend.adapter.out.persistence.activity.toDomain
import com.soomsoom.backend.application.port.`in`.activity.dto.InstructorActivitySummaryResult
import com.soomsoom.backend.application.port.`in`.activity.query.SearchInstructorActivitiesCriteria
import com.soomsoom.backend.application.port.`in`.activity.usecase.query.SearchInstructorActivitiesUseCase
import com.soomsoom.backend.application.port.out.activity.ActivityPort
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class SearchInstructorActivitiesService(
    private val activityPort: ActivityPort,
) : SearchInstructorActivitiesUseCase {
    override fun search(criteria: SearchInstructorActivitiesCriteria, pageable: Pageable): Page<InstructorActivitySummaryResult> {
        val activityDtoPage = activityPort.searchByInstructorIdWithFavoriteStatus(criteria, pageable)

        return activityDtoPage.map { dto ->
            val activity = dto.activity.toDomain()
            InstructorActivitySummaryResult.from(activity, dto.isFavorited)
        }
    }
}
