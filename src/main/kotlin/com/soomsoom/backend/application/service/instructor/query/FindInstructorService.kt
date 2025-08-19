package com.soomsoom.backend.application.service.instructor.query

import com.soomsoom.backend.application.port.`in`.instructor.dto.FindInstructorResult
import com.soomsoom.backend.application.port.`in`.instructor.query.SearchInstructorsCriteria
import com.soomsoom.backend.application.port.`in`.instructor.usecase.query.FindInstructorByIdUseCase
import com.soomsoom.backend.application.port.`in`.instructor.usecase.query.SearchInstructorUseCase
import com.soomsoom.backend.application.port.out.instructor.InstructorPort
import com.soomsoom.backend.common.exception.SoomSoomException
import com.soomsoom.backend.domain.common.DeletionStatus
import com.soomsoom.backend.domain.instructor.InstructorErrorCode
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class FindInstructorService(
    private val instructorPort: InstructorPort,
) : FindInstructorByIdUseCase, SearchInstructorUseCase {

    /**
     * ID로 강사 한 명을 조회
     */
    @PreAuthorize("hasRole('ADMIN') or #deletionStatus.name() == 'ACTIVE'")
    override fun findById(instructorId: Long, deletionStatus: DeletionStatus): FindInstructorResult {
        return instructorPort.findById(instructorId, deletionStatus)
            ?. let(FindInstructorResult::from)
            ?: throw SoomSoomException(InstructorErrorCode.NOT_FOUND)
    }

    /**
     * 검색 조건을 통해 여러 강사 조회
     */
    @PreAuthorize("hasRole('ADMIN') or #criteria.deletionStatus.name == 'ACTIVE'")
    override fun search(criteria: SearchInstructorsCriteria, pageable: Pageable): Page<FindInstructorResult> {
        return instructorPort.search(criteria, pageable).map { FindInstructorResult.from(it) }
    }
}
