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
    override fun findById(instructorId: Long, userId: Long, deletionStatus: DeletionStatus): FindInstructorResult {
        val dto = instructorPort.findWithFollowStatusById(instructorId, userId, deletionStatus)
            ?: throw SoomSoomException(InstructorErrorCode.NOT_FOUND)

        // DTO 내부의 엔티티를 도메인 객체로 변환
        val instructor = dto.instructor.toDomain()

        // 도메인 객체와 팔로우 상태를 조합하여 최종 Result DTO를 생성하고 반환
        return FindInstructorResult.from(instructor, dto.isFollowing)
    }

    /**
     * 검색 조건을 통해 여러 강사 조회
     */
    @PreAuthorize("hasRole('ADMIN') or #criteria.deletionStatus.name == 'ACTIVE'")
    override fun search(criteria: SearchInstructorsCriteria, pageable: Pageable): Page<FindInstructorResult> {
        val instructorDtoPage = instructorPort.searchWithFollowStatus(criteria, pageable)

        // DTO 페이지를 최종 Result DTO 페이지로 변환하여 반환
        return instructorDtoPage.map { dto ->
            val instructor = dto.instructor.toDomain()
            FindInstructorResult.from(instructor, dto.isFollowing)
        }
    }
}
