package com.soomsoom.backend.application.service.activity.query

import com.soomsoom.backend.adapter.out.persistence.activity.toDomain
import com.soomsoom.backend.application.port.`in`.activity.dto.ActivityResult
import com.soomsoom.backend.application.port.`in`.activity.dto.ActivitySummaryResult
import com.soomsoom.backend.application.port.`in`.activity.query.SearchActivitiesCriteria
import com.soomsoom.backend.application.port.`in`.activity.usecase.query.FindActivityUseCase
import com.soomsoom.backend.application.port.`in`.activity.usecase.query.SearchActivitiesUseCase
import com.soomsoom.backend.application.port.out.activity.ActivityPort
import com.soomsoom.backend.common.exception.SoomSoomException
import com.soomsoom.backend.domain.activity.ActivityErrorCode
import com.soomsoom.backend.domain.common.DeletionStatus
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class FindActivityService(
    private val activityPort: ActivityPort,
) : FindActivityUseCase, SearchActivitiesUseCase {

    /**
     * 단 건 조회
     */
    @PreAuthorize("hasRole('ADMIN') or #deletionStatus.name() == 'ACTIVE' and #userId == authentication.principal.id")
    override fun findActivity(activityId: Long, userId: Long, deletionStatus: DeletionStatus): ActivityResult {
        return activityPort.findByIdWithInstructors(activityId, userId, deletionStatus)
            ?. let { dto ->
                val activity = dto.activity.toDomain()
                val author = dto.author.toDomain()
                val narrator = dto.narrator.toDomain()
                ActivityResult.from(activity, author, narrator, dto.isFavorited)
            }
            ?: throw SoomSoomException(ActivityErrorCode.NOT_FOUND)
    }

    /**
     * 다 건 조회
     */
    @PreAuthorize("hasRole('ADMIN') or #criteria.deletionStatus.name == 'ACTIVE' and #criteria.userId == authentication.principal.id")
    override fun search(criteria: SearchActivitiesCriteria, pageable: Pageable): Page<ActivitySummaryResult> {
        return activityPort.search(criteria, pageable)
    }
}
