package com.soomsoom.backend.application.port.out.instructor

import com.soomsoom.backend.adapter.out.persistence.instructor.repository.jpa.dto.InstructorWithFollowStatusDto
import com.soomsoom.backend.application.port.`in`.instructor.query.SearchInstructorsCriteria
import com.soomsoom.backend.domain.common.DeletionStatus
import com.soomsoom.backend.domain.instructor.model.Instructor
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface InstructorPort {
    fun findById(instructorId: Long, deletionStatus: DeletionStatus = DeletionStatus.ACTIVE): Instructor?
    fun search(criteria: SearchInstructorsCriteria, pageable: Pageable): Page<Instructor>
    fun save(instructor: Instructor): Instructor
    fun findWithFollowStatusById(
        instructorId: Long,
        userId: Long,
        deletionStatus: DeletionStatus = DeletionStatus.ACTIVE,
    ): InstructorWithFollowStatusDto?
    fun searchWithFollowStatus(criteria: SearchInstructorsCriteria, pageable: Pageable): Page<InstructorWithFollowStatusDto>
}
