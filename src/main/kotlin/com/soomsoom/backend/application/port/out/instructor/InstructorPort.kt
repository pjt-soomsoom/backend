package com.soomsoom.backend.application.port.out.instructor

import com.soomsoom.backend.adapter.`in`.web.api.instructor.request.InstructorSearchCriteria
import com.soomsoom.backend.domain.common.DeletionStatus
import com.soomsoom.backend.domain.instructor.model.Instructor
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface InstructorPort {
    fun findById(instructorId: Long, deletionStatus: DeletionStatus = DeletionStatus.ACTIVE): Instructor?
    fun search(criteria: InstructorSearchCriteria, pageable: Pageable): Page<Instructor>
    fun save(instructor: Instructor): Instructor
}
