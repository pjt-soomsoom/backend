package com.soomsoom.backend.application.port.out.activity

import com.soomsoom.backend.adapter.out.persistence.activity.repository.jpa.dto.ActivityWithFavoriteStatusDto
import com.soomsoom.backend.adapter.out.persistence.activity.repository.jpa.dto.ActivityWithInstructorsDto
import com.soomsoom.backend.application.port.`in`.activity.dto.ActivitySummaryResult
import com.soomsoom.backend.application.port.`in`.activity.query.SearchActivitiesCriteria
import com.soomsoom.backend.application.port.`in`.activity.query.SearchInstructorActivitiesCriteria
import com.soomsoom.backend.domain.activity.model.Activity
import com.soomsoom.backend.domain.common.DeletionStatus
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface ActivityPort {
    fun findByIdWithInstructors(id: Long, userId: Long, deletionStatus: DeletionStatus = DeletionStatus.ACTIVE): ActivityWithInstructorsDto?
    fun search(criteria: SearchActivitiesCriteria, pageable: Pageable): Page<ActivitySummaryResult>
    fun save(activity: Activity): Activity
    fun findById(id: Long, deletionStatus: DeletionStatus = DeletionStatus.ACTIVE): Activity?
    fun searchByInstructorIdWithFavoriteStatus(criteria: SearchInstructorActivitiesCriteria, pageable: Pageable): Page<ActivityWithFavoriteStatusDto>
    fun findByIds(activityIds: List<Long>): List<Activity>
}
